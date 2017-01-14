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
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/app.xml"}, false)) {
            ctx.addProtocolResolver(new ScriptProtocolResolver(Paths.get(System.getProperty("scripts.base.folder"))));
            ctx.refresh();

            logger.info("Getting beans...");
            final List<Object> javaBeans = ctx.getBean("javaBeans", List.class);
            logger.info("Running beans...");
            for (Object bean : javaBeans) {
                new AnyObject(bean)
                        .run();
            }

//            new AnyObject(ctx.getBean("javaBean"))
//                    .run();

            logger.info("And groovy bean...");
            new AnyObject(ctx.getBean("groovyBean"))
                    .run();

            logger.info("Done!");
        }
    }
}
