package com.github.ykiselev.compilation;

import javax.tools.*;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ClassFactory {

    ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits) throws IOException;

    /**
     *
     */
    final class Default implements ClassFactory {

        private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        private final ClassLoader parent;

        private final Charset charset;

        private final Writer out;

        public Default(ClassLoader parent, Charset charset, Writer out) {
            this.parent = Objects.requireNonNull(parent);
            this.charset = Objects.requireNonNull(charset);
            this.out = out;
        }

        public Default(ClassLoader parent, Charset charset) {
            this(parent, charset, null);
        }

        private JavaFileManager createFileManager(ClassStorage storage) {
            return new StorageBackedJavaFileManager(
                    compiler.getStandardFileManager(
                            this::report,
                            null,
                            charset
                    ),
                    storage
            );
        }

        @Override
        public ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits) throws IOException {
            final ClassStorage.Default storage = new ClassStorage.Default(parent);
            try (JavaFileManager fileManager = createFileManager(storage)) {
                final JavaCompiler.CompilationTask task = compiler.getTask(
                        out,
                        fileManager,
                        this::report,
                        null,
                        null,
                        compilationUnits
                );
                if (task.call() != Boolean.TRUE) {
                    throw new IllegalStateException("Compilation failed!");
                }
            }
            return storage.classLoader();
        }

        private void report(Diagnostic<? extends JavaFileObject> d) {
            if (out == null) {
                return;
            }
            try {
                out.append(new StringBuilder()
                        .append(d.getCode()).append(' ')
                        .append(d.getKind()).append(" at ")
                        .append(d.getPosition()).append(" (")
                        .append(d.getStartPosition()).append('-').append(d.getEndPosition()).append(") in ")
                        .append(d.getSource()).append(": ").append(d.getMessage(Locale.getDefault()))
                        .append('\n')
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
