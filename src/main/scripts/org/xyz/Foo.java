package org.xyz;

import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Function<String, Double> {

    @Override
    public Double apply(String s) {
        final A a = new A(123);
        final B b = new B("abc");
        System.out.println("a=" + a + ", b=" + b);
        return Double.parseDouble(s);
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
