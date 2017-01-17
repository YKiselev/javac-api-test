package com.github.ykiselev.compilation.source;

import org.apache.commons.io.FilenameUtils;

import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

    private final Charset charset;

    public DiskSourceStorage(Path base, Charset charset) {
        this.base = Objects.requireNonNull(base).toAbsolutePath()
                .normalize();
        this.charset = Objects.requireNonNull(charset);
    }

    public DiskSourceStorage(String base, Charset charset) {
        this(Paths.get(base), charset);
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
    public JavaFileObject resolve(String fileName) throws IOException {
        return fromPath(
                base.resolve(fileName)
        );
    }

    private JavaFileObject fromPath(Path path) {
        return new FileObject(
                new UrlJavaSource(
                        path.normalize().toUri(),
                        JavaFileObject.Kind.SOURCE,
                        charset
                )
        );
    }

    /**
     *
     */
    private class FileObject extends ForwardingJavaFileObject<JavaFileObject> implements HasBinaryName {

        private FileObject(JavaFileObject fileObject) {
            super(fileObject);
        }

        @Override
        public String binaryName() {
            return FilenameUtils.removeExtension(
                    base.relativize(
                            Paths.get(toUri())
                    ).toString()
            ).replace(File.separator, ".");
        }
    }
}
