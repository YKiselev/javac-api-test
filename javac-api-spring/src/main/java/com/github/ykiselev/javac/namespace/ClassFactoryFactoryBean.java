package com.github.ykiselev.javac.namespace;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.javac.spi.SourceStorageFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.Ordered;

import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ClassFactoryFactoryBean extends AbstractFactoryBean<ClassFactory> implements Ordered {

    private static final String BEAN = "com.github.ykiselev.javac.namespace.classFactoryFactoryBean";

    private final Logger logger = LogManager.getLogger(getClass());

    private SourceStorage sourceStorage;

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    protected ClassFactory createInstance() throws Exception {
        final ServiceLoader<SourceStorageFactory> loader = ServiceLoader.load(SourceStorageFactory.class);
        for (SourceStorageFactory.Media media : getMediaList()) {
            logger.debug("Trying {} media...", media);
            for (SourceStorageFactory factory : loader) {
                logger.debug("Trying {}...", factory);
                if (factory.getMedia() == media) {
                    logger.debug("Using {} to create object...", factory);
                    this.sourceStorage = factory.create();
                    break;
                }
            }
        }
        if (sourceStorage == null) {
            throw new FactoryBeanNotInitializedException("Source storage SPI is not configured!");
        }
        logger.debug("Creating class factory...");
        return new ClassFactory.Default(
                sourceStorage,
                new OutputStreamWriter(System.out)
        );
    }

    private List<SourceStorageFactory.Media> getMediaList() {
        final String rawMediaList = System.getProperty("scripts.storageMediaList");
        if (StringUtils.isEmpty(rawMediaList)) {
            return Collections.singletonList(SourceStorageFactory.Media.FS);
        }
        return Arrays.stream(rawMediaList.split(","))
                .map(StringUtils::trim)
                .filter(Objects::nonNull)
                .map(SourceStorageFactory.Media::valueOf)
                .collect(Collectors.toList());
    }

    static synchronized String register(BeanDefinitionRegistry registry) {
        // Check all parent contexts for presence of ClassFactoryFactoryBean
        Object r = registry;
        while (r instanceof HierarchicalBeanFactory) {
            final BeanFactory parent = ((HierarchicalBeanFactory) r).getParentBeanFactory();
            if (parent != null && parent.containsBean(BEAN)) {
                return BEAN;
            }
            r = parent;
        }
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
