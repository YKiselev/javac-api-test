package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.google.common.collect.ImmutableList;

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

    ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits, ClassStorage classStorage) throws CompilationException, IOException;

    /**
     *
     */
    final class Default implements ClassFactory {

        private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        private final Writer out;

        private final SourceStorage sourceStorage;

        private final Charset charset;

        public Default(SourceStorage sourceStorage, Writer out, Charset charset) {
            this.sourceStorage = Objects.requireNonNull(sourceStorage);
            this.out = Objects.requireNonNull(out);
            this.charset = Objects.requireNonNull(charset);
        }

        private JavaFileManager createFileManager(ClassStorage storage) {
            return new StorageBackedJavaFileManager(
                    compiler.getStandardFileManager(
                            this::report,
                            null,
                            null
                    ),
                    sourceStorage,
                    storage,
                    charset
            );
        }

        @Override
        public ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits, ClassStorage classStorage) throws CompilationException, IOException {
            try (JavaFileManager fileManager = createFileManager(classStorage)) {
                final JavaCompiler.CompilationTask task = compiler.getTask(
                        out,
                        fileManager,
                        this::report,
                        ImmutableList.of("-proc:none"),
                        null,
                        compilationUnits
                );
                if (task.call() != Boolean.TRUE) {
                    throw new CompilationException("Compilation failed! See log for details.");
                }
            }
            return classStorage.classLoader();
        }

        private void report(Diagnostic<? extends JavaFileObject> d) {
            if (out == null) {
                return;
            }
            final StringBuilder sb = new StringBuilder();
            final JavaFileObject src = d.getSource();
            if (src != null) {
                sb.append(src.getName());
            }
            sb.append(" [")
                    .append(d.getPosition()).append(" (")
                    .append(d.getStartPosition()).append("..").append(d.getEndPosition())
                    .append("] ").append(d.getKind());

            sb.append(": ").append(d.getMessage(Locale.getDefault()));
            sb.append('\n');
            try {
                out.append(sb);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}