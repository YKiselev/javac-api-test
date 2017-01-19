package com.github.ykiselev.spring;

import com.google.common.base.Throwables;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class FileBasedProtocolResolver implements ProtocolResolver {

    private final String protocol;

    private final Path base;

    public FileBasedProtocolResolver(Path base, String protocol) {
        this.base = Objects.requireNonNull(base);
        this.protocol = Objects.requireNonNull(protocol);
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith(protocol)) {
            try {
                return new UrlResource(
                        base.resolve(
                                location.substring(protocol.length())
                        ).toAbsolutePath()
                                .normalize()
                                .toFile()
                                .toURI()
                                .toURL()
                                .toString()
                );
            } catch (MalformedURLException e) {
                throw Throwables.propagate(e);
            }
        }
        return null;
    }
}
