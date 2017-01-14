package com.github.ykiselev.spring;

import com.github.ykiselev.AnyObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.file.Paths;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SpringApp {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/app.xml"}, false)) {
            ctx.addProtocolResolver(new ScriptProtocolResolver(Paths.get(System.getProperty("scripts.base.folder"))));
            ctx.addBeanFactoryPostProcessor(new BeanDefinitionRegistryPostProcessor() {
                @Override
                public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
                    //final BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition()
                    //registry.registerBeanDefinition("", new RootBeanDefinition());
                }

                @Override
                public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                }
            });
            ctx.refresh();

            new AnyObject(ctx.getBean("javaBean"))
                    .run();

            new AnyObject(ctx.getBean("groovyBean"))
                    .run();
        }
    }
}
