package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.google.common.collect.ImmutableSet;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class TrackingClassFactory implements ClassFactory {

    private final ClassFactory delegate;

    private final Set<ClassLoader> trackedClassLoaders = Collections.newSetFromMap(
            new WeakHashMap<ClassLoader, Boolean>()
    );

    public TrackingClassFactory(ClassFactory delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Override
    public ClassLoader compile(Iterable<? extends JavaFileObject> compilationUnits, ClassStorage classStorage) throws CompilationException, IOException {
        final ClassLoader result = delegate.compile(compilationUnits, classStorage);
        synchronized (trackedClassLoaders) {
            trackedClassLoaders.add(result);
        }
        return result;
    }

    public Set<ClassLoader> getTrackedClassLoaders() {
        synchronized (trackedClassLoaders) {
            return ImmutableSet.copyOf(trackedClassLoaders);
        }
    }
}
