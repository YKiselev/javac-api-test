package com.github.ykiselev.javac.namespace;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.javac.spi.SourceStorageFactory;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;

import java.io.OutputStreamWriter;
import java.util.ServiceLoader;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClassFactoryFactoryBean extends AbstractFactoryBean<ClassFactory> implements Ordered {

    static final String BEAN = "com.github.ykiselev.javac.namespace.classFactoryFactoryBean";

    private SourceStorage sourceStorage;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    protected ClassFactory createInstance() throws Exception {
        final ServiceLoader<SourceStorageFactory> loader = ServiceLoader.load(SourceStorageFactory.class);
        for (SourceStorageFactory factory : loader) {
            this.sourceStorage = factory.create();
            break;
        }
        if (sourceStorage == null) {
            throw new FactoryBeanNotInitializedException("Source storage SPI is not configured!");
        }
        return new ClassFactory.Default(
                sourceStorage,
                new OutputStreamWriter(System.out)
        );
    }

    static synchronized String register(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(BEAN)) {
            registry.registerBeanDefinition(BEAN, new RootBeanDefinition(ClassFactoryFactoryBean.class));
        }
        return BEAN;
    }

    @Override
    public Class<?> getObjectType() {
        return ClassFactory.class;
    }
}
