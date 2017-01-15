package com.github.ykiselev.javac.factories;

import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.javac.spi.SourceStorageFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.FactoryBean;

import java.util.ServiceLoader;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SourceStorageFactoryBean implements FactoryBean<SourceStorage> {

    @Override
    public SourceStorage getObject() throws Exception {
        final ServiceLoader<SourceStorageFactory> loader = ServiceLoader.load(SourceStorageFactory.class);
        for (SourceStorageFactory factory : loader) {
            return factory.create();
        }
        throw new BeanCreationException("Source storage factory is not configured!");
    }

    @Override
    public Class<?> getObjectType() {
        return SourceStorage.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
