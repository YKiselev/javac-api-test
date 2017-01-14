package org.xyz

import groovy.transform.CompileStatic

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@CompileStatic
class Predicate implements java.util.function.Predicate<String> {

    @Override
    boolean test(String s) {
        s?.length() > 3
    }
}
