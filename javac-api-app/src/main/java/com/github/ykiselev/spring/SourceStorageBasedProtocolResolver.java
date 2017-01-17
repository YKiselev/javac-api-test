package com.github.ykiselev.spring;

import com.github.ykiselev.compilation.source.SourceStorage;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SourceStorageBasedProtocolResolver implements ProtocolResolver {

    private final String protocol;

    private final SourceStorage storage;

    public SourceStorageBasedProtocolResolver(String protocol, SourceStorage storage) {
        Preconditions.checkArgument(
                protocol.endsWith(":"), "Protocol should end with colon character!"
        );
        this.protocol = Objects.requireNonNull(protocol);
        this.storage = Objects.requireNonNull(storage);
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith(protocol)) {
            final String fileName = location.substring(protocol.length());
            try {
                return new ByteArrayResource(
                        storage.resolve(fileName)
                                .getCharContent(false)
                                .toString()
                                .getBytes(StandardCharsets.UTF_8),
                        location
                ) {
                    @Override
                    public String getFilename() {
                        return fileName;
                    }
                };
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }
        return null;
    }
}
