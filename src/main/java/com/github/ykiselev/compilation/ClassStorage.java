package com.github.ykiselev.compilation;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ClassStorage {

    void put(String className, Supplier<ByteBuffer> value);

    ClassLoader classLoader();

    /**
     * Default implementation of storage, backed by {@link ConcurrentHashMap}
     */
    final class Default implements ClassStorage {

        private final Map<String, Supplier<ByteBuffer>> map = new ConcurrentHashMap<>();

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
        public void put(String className, Supplier<ByteBuffer> value) {
            map.put(className, value);
        }

        @Override
        public ClassLoader classLoader() {
            return classLoader;
        }
    }
}
