package org.xyz;

import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Function<String, Double> {

    @Override
    public Double apply(String s) {
        return Double.parseDouble(s);
    }
}
