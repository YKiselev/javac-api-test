package com.github.ykiselev;

import com.github.ykiselev.model.Component;
import com.github.ykiselev.model.Group;
import com.github.ykiselev.model.Position;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Code to call some method on any supported object (like Runnable#run(), Callable#call(), etc).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AnyObject implements Runnable {

    private final Logger logger = LogManager.getLogger(getClass());

    private final Object object;

    public AnyObject(Object object) {
        this.object = Objects.requireNonNull(object);
    }

    @Override
    public void run() {
        logger.info("=================================");
        try {
            if (object instanceof Function) {
                final Function<Iterable<Position>, Component> function = Function.class.cast(object);
                final Component component = function.apply(
                        Positions.prepare(10_000)
                );
                logger.info("Calculated component \"{}\"", component.name());
                for (Group group : component.groups()) {
                    logger.info("  {}", group);
                }
            } else if (object instanceof Runnable) {
                ((Runnable) object).run();
            } else if (object instanceof Callable) {
                logger.info(((Callable) object).call());
            }
        } catch (Exception e) {
            logger.error("Call failed!", e);
        }
        logger.info("=================================");
    }
}
