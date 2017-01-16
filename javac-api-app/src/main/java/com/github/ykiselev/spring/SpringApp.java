package com.github.ykiselev.spring;

import com.github.ykiselev.AnyObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ProtocolResolver;

import java.nio.file.Paths;
import java.util.List;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class SpringApp {

    private final Logger logger = LogManager.getLogger(getClass());

    public static void main(String[] args) {
        new SpringApp().run();
    }

    private void run() {
        logger.info("Starting...");
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/app.xml"}, false)) {
            ctx.addProtocolResolver(createScriptProtocolResolver());
            ctx.refresh();

            logger.info("Getting beans...");
            final List<Object> javaBeans = ctx.getBean("javaBeans", List.class);
            logger.info("Running beans...");
            for (Object bean : javaBeans) {
                new AnyObject(bean)
                        .run();
            }

            logger.info("And groovy bean...");
            new AnyObject(ctx.getBean("groovyBean"))
                    .run();

            logger.info("Done!");
        }
    }

    private ProtocolResolver createScriptProtocolResolver() {
        return new ScriptProtocolResolver(
                Paths.get(
                        System.getProperty("scripts.baseFolder")
                )
        );
    }
}
