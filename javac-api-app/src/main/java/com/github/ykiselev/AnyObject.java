package com.github.ykiselev;

import com.github.ykiselev.model.Component;
import com.github.ykiselev.model.Position;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Code to call some method on any supported object (like Runnable#run(), Callable#call(), etc).
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class AnyObject implements Runnable {

    private final Object object;

    public AnyObject(Object object) {
        this.object = Objects.requireNonNull(object);
    }

    @Override
    public void run() {
        System.out.println("=================================");
        if (object instanceof Function) {
            final Function<Iterable<Position>, Component> function = Function.class.cast(object);
            final Component component = function.apply(
                    Positions.prepare(10_000)
            );

            System.out.println(component);
        } else if (object instanceof Runnable) {
            ((Runnable) object).run();
        } else if (object instanceof Callable) {
            try {
                System.out.println(((Callable) object).call());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("=================================");
    }
}
