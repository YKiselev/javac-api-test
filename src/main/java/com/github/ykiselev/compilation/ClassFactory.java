package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.google.common.collect.ImmutableList;

import javax.tools.*;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ClassFactory {

    ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits, ClassStorage classStorage) throws CompilationException;

    /**
     *
     */
    final class Default implements ClassFactory {

        private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        private final Writer out;

        private final SourceStorage sourceStorage;

        public Default(SourceStorage sourceStorage, Writer out) {
            this.sourceStorage = Objects.requireNonNull(sourceStorage);
            this.out = out;
        }

        public Default(SourceStorage sourceStorage) {
            this(sourceStorage, null);
        }

        private JavaFileManager createFileManager(ClassStorage storage) throws IOException {
            return new StorageBackedJavaFileManager(
                    compiler.getStandardFileManager(
                            this::report,
                            null,
                            null
                    ),
                    sourceStorage,
                    storage
            );
        }

        @Override
        public ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits, ClassStorage classStorage) throws CompilationException {
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
                    out.flush();
                    throw new CompilationException("Compilation failed! See log for details.");
                }
            } catch (IOException ex) {
                throw new CompilationException(ex);
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
