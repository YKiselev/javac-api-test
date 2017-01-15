package com.github.ykiselev.javac.factories;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.CompilationException;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.scripting.ScriptSource;

import javax.tools.JavaFileObject.Kind;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavacBeanFactory<I> extends AbstractFactoryBean<I> {

    private final ClassFactory classFactory;

    private final ScriptSource resource;

    private final Map<String, Object> properties;

    public JavacBeanFactory(ClassFactory classFactory, ScriptSource resource, Map<String, Object> properties) {
        this.classFactory = Objects.requireNonNull(classFactory);
        this.resource = Objects.requireNonNull(resource);
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    protected I createInstance() throws Exception {
        final String className = resource.suggestedClassName();
        final ClassLoader classLoader = compile(
                className,
                resource.getScriptAsString()
        );
        final Object obj = classLoader.loadClass(className)
                .newInstance();
        setProperties(obj);
        return (I) obj;
    }

    private void setProperties(Object object) throws InvocationTargetException, IllegalAccessException {
        if (properties.isEmpty()) {
            return;
        }
        BeanUtils.populate(object, properties);
    }

    private ClassLoader compile(String className, String source) throws CompilationException {
        return classFactory.compile(
                Collections.singletonList(
                        new StringJavaSource(
                                URI.create("resource:///" + className.replaceAll("\\.", "/") + Kind.SOURCE.extension),
                                Kind.SOURCE,
                                source
                        )
                ),
                new ClassStorage.Default(
                        getClass().getClassLoader()
                )
        );
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

}
