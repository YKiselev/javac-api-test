package com.github.ykiselev.javac.factories;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import com.github.ykiselev.javac.spi.SourceStorageFactory;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import javax.tools.JavaFileObject;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavacBeanFactory<I> implements FactoryBean<I> {

    private final Class<I> targetClass;

    private final Resource resource;

    private final URI uri;

    private final Map<String, Object> properties;

    public JavacBeanFactory(Class<I> targetClass, Resource resource, URI uri, Map<String, Object> properties) {
        this.targetClass = targetClass;
        this.resource = Objects.requireNonNull(resource);
        this.uri = Objects.requireNonNull(uri);
        this.properties = Objects.requireNonNull(properties);
    }

    private void setProperties(Object object) throws InvocationTargetException, IllegalAccessException {
        if (properties.isEmpty()) {
            return;
        }
        BeanUtils.populate(object, properties);
    }

    private SourceStorage createSourceStorage() {
        final ServiceLoader<SourceStorageFactory> loader = ServiceLoader.load(SourceStorageFactory.class);
        for (SourceStorageFactory factory : loader) {
            return factory.create();
        }
        throw new IllegalStateException("Storage factory not found!");
    }

    private String createClassName(String source) {
        String result = uri.getSchemeSpecificPart().replaceAll("\\\\|/", ".");
        if (StringUtils.endsWithIgnoreCase(result, ".java")) {
            result = result.substring(0, result.length() - 5);
        }
        return result;
    }

    @Override
    public I getObject() throws Exception {
        final String source = IOUtils.toString(
                resource.getInputStream(),
                StandardCharsets.UTF_8
        );
        final String className = createClassName(source);
        final SourceStorage sourceStorage = createSourceStorage();
        final ClassFactory classFactory = new ClassFactory.Default(
                sourceStorage,
                new OutputStreamWriter(System.out)
        );
        final ClassLoader classLoader = classFactory.compile(
                Collections.singletonList(
                        new StringJavaSource(
                                URI.create("string:///" + uri.getSchemeSpecificPart()),
                                JavaFileObject.Kind.SOURCE,
                                source
                        )
                ),
                new ClassStorage.Default(
                        getClass().getClassLoader()
                )
        );
        final Object obj = classLoader.loadClass(className)
                .newInstance();
        setProperties(obj);
        return (I) obj;
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
