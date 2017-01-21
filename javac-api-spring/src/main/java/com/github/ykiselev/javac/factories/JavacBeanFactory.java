package com.github.ykiselev.javac.factories;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.CompilationException;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import com.google.common.base.Throwables;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.scripting.ScriptSource;

import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavacBeanFactory extends AbstractFactoryBean<Object> {

    private final ClassFactory classFactory;

    private final ScriptSource resource;

    private final Map<String, Object> properties;

    public JavacBeanFactory(ClassFactory classFactory, ScriptSource resource, Map<String, Object> properties) {
        this.classFactory = Objects.requireNonNull(classFactory);
        this.resource = Objects.requireNonNull(resource);
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    protected Object createInstance() throws Exception {
        final String className = resource.suggestedClassName();
        final ClassLoader classLoader = compile(
                className,
                resource.getScriptAsString()
        );
        final Object obj = classLoader.loadClass(className)
                .newInstance();
        setProperties(obj);
        return obj;
    }

    private void setProperties(Object object) throws InvocationTargetException, IllegalAccessException {
        if (properties.isEmpty()) {
            return;
        }
        final BeanUtilsBean2 beanUtils = new BeanUtilsBean2();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            try {
                beanUtils.getPropertyUtils().setProperty(object, entry.getKey(), entry.getValue());
            } catch (NoSuchMethodException e) {
                throw Throwables.propagate(e);
            }
        }
        //BeanUtils.populate(object, properties);
    }

    private ClassLoader compile(String className, String source) throws CompilationException, IOException {
        return classFactory.compile(
                Collections.singletonList(
                        new StringJavaSource(
                                className,
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
