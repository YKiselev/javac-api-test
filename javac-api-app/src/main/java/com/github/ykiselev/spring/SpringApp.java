package com.github.ykiselev.spring;

import com.github.ykiselev.AnyObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        final ScriptProtocolResolver scriptProtocolResolver = new ScriptProtocolResolver(
                Paths.get(
                        System.getProperty("scripts.base.folder")
                )
        );
        try (ClassPathXmlApplicationContext parent = new ClassPathXmlApplicationContext(new String[]{"/parent.xml"}, false)) {
            parent.addProtocolResolver(scriptProtocolResolver);
            parent.refresh();
            new AnyObject(parent.getBean("javaBean0"))
                    .run();

            try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/app.xml"}, false, parent)) {
                ctx.addProtocolResolver(scriptProtocolResolver);
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
    }
}
