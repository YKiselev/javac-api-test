package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ByteCodeStorage;
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

    private final ByteCodeStorage byteCodeStorage;

    public ByteCodeFactoryBackedClassLoader(ClassLoader parent, ByteCodeStorage byteCodeStorage) {
        super(parent);
        this.byteCodeStorage = Objects.requireNonNull(byteCodeStorage);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final ByteBuffer buffer = byteCodeStorage.get(name);
        if (buffer != null) {
            logger.debug("Loading {}, {} bytes...", () -> name, buffer::remaining);
            return super.defineClass(name, buffer, (ProtectionDomain) null);
        }
        return super.findClass(name);
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Freeing classloader: " + this);
        super.finalize();
    }
}
