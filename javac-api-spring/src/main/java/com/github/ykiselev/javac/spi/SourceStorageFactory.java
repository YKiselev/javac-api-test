package com.github.ykiselev.javac.spi;

import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.compilation.source.SourceStorage;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SourceStorageFactory {

    SourceStorage create();

    final class Default implements SourceStorageFactory {

        @Override
        public SourceStorage create() {
            return new DiskSourceStorage(
                    Paths.get(
                            System.getProperty("scripts.base.folder")
                    ),
                    StandardCharsets.UTF_8
            );
        }
    }
}
