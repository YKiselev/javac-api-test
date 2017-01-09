package com.github.ykiselev.compilation.source;

import org.apache.commons.io.FilenameUtils;

import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class DiskSourceStorage implements SourceStorage {

    private final Path base;

    public DiskSourceStorage(Path base) {
        this.base = Objects.requireNonNull(base).toAbsolutePath()
                .normalize();
    }

    @Override
    public Iterable<JavaFileObject> list(String packageName, boolean recurse) throws IOException {
        final Path from = base.resolve(packageName.replace(".", "/")).normalize();
        if (!Files.exists(from)) {
            return Collections.emptyList();
        }
        final int maxDepth = recurse ? Integer.MAX_VALUE : 1;
        try (Stream<Path> paths = Files.find(from, maxDepth, (p, t) -> t.isRegularFile())) {
            return paths.map(this::fromPath)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public JavaFileObject resolve(String className) throws IOException {
        return fromPath(
                base.resolve(classNameToFileName(className))
                        .normalize()
        );
    }

    private String classNameToFileName(String className) {
        return className.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension;
    }

    private JavaFileObject fromPath(Path path) {
        return new FileObject(
                new UrlJavaSource(
                        path.toUri(),
                        JavaFileObject.Kind.SOURCE
                )
        );
    }

    /**
     *
     */
    private class FileObject extends ForwardingJavaFileObject<JavaFileObject> implements HasBinaryName {

        private FileObject(JavaFileObject fileManager) {
            super(fileManager);
        }

        @Override
        public String binaryName() {
            final String relative = base.relativize(
                    Paths.get(toUri())
            ).toString();
            return FilenameUtils.removeExtension(relative)
                    .replace(File.separator, ".");
        }
    }
}
