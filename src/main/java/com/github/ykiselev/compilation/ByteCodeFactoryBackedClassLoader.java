package com.github.ykiselev.compilation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteCodeFactoryBackedClassLoader extends SecureClassLoader {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ByteCodeFactory byteCodeFactory;

    public ByteCodeFactoryBackedClassLoader(ClassLoader parent, ByteCodeFactory byteCodeFactory) {
        super(parent);
        this.byteCodeFactory = Objects.requireNonNull(byteCodeFactory);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final ByteBuffer buffer = byteCodeFactory.get(name);
        if (buffer != null) {
            logger.debug("Loading {}, {} bytes...", () -> name, buffer::remaining);
            return super.defineClass(name, buffer, (ProtectionDomain) null);
        }
        return super.findClass(name);
    }

}
