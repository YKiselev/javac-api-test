package org.xyz;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Bar implements Callable<String> {

    @Override
    public String call() throws Exception {
        System.out.println("Let's call another script!");
        new Foo().run();

        System.out.println("Now I'll return something to caller...");
        return "Why are you calling me now? It's " + new Date();
    }
}
