package com.github.ykiselev.javac.factories;

import org.springframework.beans.factory.FactoryBean;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavacBeanFactory<I> implements FactoryBean<I> {

    private final Class<I> targetClass;

    private final String source;

    public JavacBeanFactory(Class<I> targetClass, String source) {
        this.targetClass = targetClass;
        this.source = Objects.requireNonNull(source);
    }

    @Override
    public I getObject() throws Exception {
        //classFactory.compile()
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getObjectType() {
        return targetClass;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
