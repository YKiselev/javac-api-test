package com.github.ykiselev.javac.namespace;

import com.github.ykiselev.javac.factories.JavacBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class BeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return JavacBeanFactory.class;
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        builder.addConstructorArgValue(element.getAttribute("targetClass"));
        builder.addConstructorArgValue(element.getAttribute("script-source")); // inject as resource
        builder.addConstructorArgValue(element.getAttribute("script-source")); // inject as uri to infer class name

        final BeanDefinitionBuilder properties = BeanDefinitionBuilder.rootBeanDefinition(LinkedHashMap.class);
        for (Element child : DomUtils.getChildElements(element)) {
            parserContext.getDelegate().parsePropertyElement(child, properties.getRawBeanDefinition());
        }
        final ManagedMap<String, Object> map = new ManagedMap<>();
        map.setKeyTypeName(String.class.getName());
        map.setValueTypeName(Object.class.getName());

        properties.getRawBeanDefinition()
                .getPropertyValues()
                .getPropertyValueList()
                .forEach(e -> map.put(e.getName(), e.getValue()));

        builder.addConstructorArgValue(map);
    }

}