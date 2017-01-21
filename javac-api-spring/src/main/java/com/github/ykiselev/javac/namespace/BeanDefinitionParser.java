package com.github.ykiselev.javac.namespace;

import com.github.ykiselev.javac.factories.JavacBeanFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.scripting.ScriptSource;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Objects;

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

        final UriScriptSource source = getSource(
                element.getAttribute("script-source"),
                parserContext
        );
        final ConstructorArgumentValues args = builder.getRawBeanDefinition().getConstructorArgumentValues();
        args.addIndexedArgumentValue(0, new RuntimeBeanReference(element.getAttribute("class-factory")));
        args.addIndexedArgumentValue(1, source);
        args.addIndexedArgumentValue(2, map);
    }

    private UriScriptSource getSource(String scriptSource, ParserContext context) {
        if (StringUtils.isEmpty(scriptSource)) {
            context.getReaderContext().error("script-source cannot be empty!", null);
        }
        return new UriScriptSource(
                context.getReaderContext()
                        .getResourceLoader()
                        .getResource(scriptSource),
                FilenameUtils.removeExtension(
                        URI.create(scriptSource)
                                .getSchemeSpecificPart()
                ).replaceAll("\\\\|/", "\\.")
        );
    }

    /**
     *
     */
    private static class UriScriptSource implements ScriptSource {

        final Resource resource;

        final String className;

        UriScriptSource(Resource resource, String className) {
            this.resource = Objects.requireNonNull(resource);
            this.className = Objects.requireNonNull(className);
        }

        @Override
        public String getScriptAsString() throws IOException {
            return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        }

        @Override
        public boolean isModified() {
            return false;
        }

        @Override
        public String suggestedClassName() {
            return className;
        }
    }

}
