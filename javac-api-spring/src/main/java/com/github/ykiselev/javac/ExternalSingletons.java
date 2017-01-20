package com.github.ykiselev.javac;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Map;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ExternalSingletons implements BeanFactoryPostProcessor {

    private final Map<String, Object> beans;

    public ExternalSingletons(Map<String, Object> beans) {
        this.beans = ImmutableMap.copyOf(beans);
    }

    public ExternalSingletons(String name, Object bean) {
        this(ImmutableMap.of(name, bean));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beans.entrySet()
                .forEach(
                        e -> beanFactory.registerSingleton(
                                e.getKey(),
                                e.getValue()
                        )
                );
    }
}
