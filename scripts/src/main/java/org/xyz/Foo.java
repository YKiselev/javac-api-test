package org.xyz;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Runnable {

    @Override
    public void run() {
        System.out.println("\nHello, I'm humble Runnable and I like you!\n");
    }

    /**
     * Internal class? Why not!
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
