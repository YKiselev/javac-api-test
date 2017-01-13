package com.github.ykiselev.compilation.compiled;

import com.github.ykiselev.compilation.ByteCodeFactoryBackedClassLoader;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ClassStorage {

    /**
     * Creates {@link JavaFileObject} and associates it with given className
     *
     * @param location  the location
     * @param className the class name
     * @param kind      the kind of file object, must be one of {@link JavaFileObject.Kind#SOURCE SOURCE} or {@link JavaFileObject.Kind#CLASS CLASS}
     * @param sibling   a file object to be used as hint for placement;
     *                  might be {@code null}
     * @return Writable {@link JavaFileObject} to store byte code of compiled class
     * @throws IOException if I/O error occurred
     */
    JavaFileObject create(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException;

    ClassLoader classLoader();

    /**
     * Default implementation of storage, backed by {@link ConcurrentHashMap}
     */
    final class Default implements ClassStorage {

        private final Map<String, ByteArrayOutput> map = new ConcurrentHashMap<>();

        private final ByteCodeFactoryBackedClassLoader classLoader;

        public Default(ClassLoader parent) {
            this.classLoader = new ByteCodeFactoryBackedClassLoader(
                    parent,
                    this::getByteCode
            );
        }

        private ByteBuffer getByteCode(String className) {
            final Supplier<ByteBuffer> item = map.get(className);
            return item != null ? item.get() : null;
        }

        @Override
        public JavaFileObject create(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return map.computeIfAbsent(
                    className,
                    s -> new ByteArrayOutput(
                            URI.create("bytes:///" + className),
                            kind
                    )
            );
        }

        @Override
        public ClassLoader classLoader() {
            return classLoader;
        }
    }
}
