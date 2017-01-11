package org.xyz;

import org.xyz.Bar;

import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Function<String, String> {

    @Override
    public String apply(String s) {
        final Bar bar = new Bar(this);
        return "Hello, user!\nIt's " + new java.util.Date() + ", btw look at this: " + com.github.ykiselev.Xyz.VALUE + ", and I've got " + bar;
    }

    public class A {

        private final int value;

        public int value() {
            return value;
        }

        public A(int value) {
            this.value = value;
        }
    }

    public static class B {

        private final String name;

        public String name() {
            return name;
        }

        public B(String name) {
            this.name = name;
        }
    }
}
