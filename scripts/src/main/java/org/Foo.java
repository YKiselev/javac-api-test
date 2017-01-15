package org;

import javax.tools.SimpleJavaFileObject;

/**
 * Another Foo to test {@link SimpleJavaFileObject#isNameCompatible(java.lang.String, javax.tools.JavaFileObject.Kind)}
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Runnable {

    @Override
    public void run() {
        System.out.println("\nHello from " + getClass().getName() + "\n");
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
