package org.xyz;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Runnable {

    @Override
    public void run() {
        System.out.println("\nHello from " + getClass().getName() + "!\n");
    }

    /**
     * Internal class.
     */
    public class A {

        private final int value;

        public int value() {
            return value;
        }

        public A(int value) {
            this.value = value;
        }
    }

}
