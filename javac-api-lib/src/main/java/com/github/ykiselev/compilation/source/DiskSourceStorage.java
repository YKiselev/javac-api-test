package com.github.ykiselev.compilation.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
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
        this.base = Objects.requireNonNull(base).normalize();
    }

    public DiskSourceStorage(String base) {
        this(Paths.get(base));
    }

    @Override
    public Collection<String> list(String packageName, boolean recurse) throws IOException {
        final Path from = base.resolve(packageName.replace(".", "/")).normalize();
        if (!Files.exists(from)) {
            return Collections.emptyList();
        }
        final int maxDepth = recurse ? Integer.MAX_VALUE : 1;
        try (Stream<Path> paths = Files.find(from, maxDepth, (p, t) -> t.isRegularFile())) {
            return paths.map(base::relativize)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public InputStream resolve(String fileName) throws IOException {
        return Files.newInputStream(
                base.resolve(fileName)
        );
    }
}
