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
public final class ScriptProtocolResolver implements ProtocolResolver {

    static final String PREFIX = "script:";

    private final Path base;

    public ScriptProtocolResolver(Path base) {
        this.base = Objects.requireNonNull(base);
    }

    @Override
    public Resource resolve(String location, ResourceLoader resourceLoader) {
        if (location.startsWith(PREFIX)) {
            try {
                return new UrlResource(
                        base.resolve(
                                location.substring(PREFIX.length())
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
