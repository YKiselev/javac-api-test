package org.xyz;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Bar {

    private final Foo foo;

    public Foo foo() {
        return foo;
    }

    public Bar(Foo foo) {
        this.foo = Objects.requireNonNull(foo);
    }

    public void say(){
        System.out.println("Hello, world!");
    }
}
