package com.github.ykiselev.javac.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavacNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("bean", new BeanDefinitionParser());
    }
}
