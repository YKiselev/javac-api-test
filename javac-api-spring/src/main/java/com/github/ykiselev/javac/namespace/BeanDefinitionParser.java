package com.github.ykiselev.javac.namespace;

import com.github.ykiselev.javac.factories.JavacBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return JavacBeanFactory.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(element.getAttribute("targetClass"));
        builder.addConstructorArgValue(element.getAttribute("source"));
    }
}
