package com.github.ykiselev.compilation;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ClassStorage<I extends Supplier<ByteBuffer>> {

    void put(String className, I value);

    ClassLoader classLoader();

    /**
     * Default implementation of storage, backed by {@link ConcurrentHashMap}
     *
     * @param <I>
     */
    final class Default<I extends Supplier<ByteBuffer>> implements ClassStorage<I> {

        private final Map<String, I> map = new ConcurrentHashMap<>();

        private final ByteCodeFactoryBackedClassLoader classLoader;

        public Default(ClassLoader parent) {
            this.classLoader = new ByteCodeFactoryBackedClassLoader(parent, this::getByteCode);
        }

        private ByteBuffer getByteCode(String className) {
            final I item = map.get(className);
            return item != null ? item.get() : null;
        }

        @Override
        public void put(String className, I value) {
            map.put(className, value);
        }

        @Override
        public ClassLoader classLoader() {
            return classLoader;
        }
    }
}
