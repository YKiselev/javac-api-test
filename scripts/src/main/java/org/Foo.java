package org;

/**
 * Another Foo
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Foo implements Runnable {

    @Override
    public void run() {
        System.out.println("\nHello from " + getClass().getName() + "\n");
    }

}
