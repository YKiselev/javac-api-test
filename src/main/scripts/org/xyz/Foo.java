package org.xyz;

import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Function<String, String> {

    @Override
    public String apply(String s) {
        return "Hello, user at " + new java.util.Date();
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
