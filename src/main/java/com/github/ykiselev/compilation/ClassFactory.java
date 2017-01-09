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

    ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits) throws IOException;

    /**
     *
     */
    final class Default implements ClassFactory {

        private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        private final ClassLoader parent;

        private final Charset charset;

        private final Writer out;

        private final SourceStorage sourceStorage;

        public Default(ClassLoader parent, SourceStorage sourceStorage, Charset charset, Writer out) {
            this.parent = Objects.requireNonNull(parent);
            this.charset = Objects.requireNonNull(charset);
            this.sourceStorage = Objects.requireNonNull(sourceStorage);
            this.out = out;
        }

        public Default(ClassLoader parent, Charset charset, SourceStorage sourceStorage) {
            this(parent, sourceStorage, charset, null);
        }

        private JavaFileManager createFileManager(ClassStorage storage) throws IOException {
            final StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(
                    this::report,
                    null,
                    charset
            );
//            stdFileManager.setLocation(
//                    StandardLocation.SOURCE_PATH,
//                    Collections.singletonList(new File("D:\\Projects\\Java\\compile-java\\src\\main\\scripts"))
//            );
            return new StorageBackedJavaFileManager(
                    stdFileManager,
                    sourceStorage,
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
                        ImmutableList.of("-proc:none"),//ImmutableList.of("-classpath", System.getProperty("java.class.path")),
                        null,
                        compilationUnits
                );
                if (task.call() != Boolean.TRUE) {
                    out.flush();
                    throw new IllegalStateException("Compilation failed!");
                }
            }
            return storage.classLoader();
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
