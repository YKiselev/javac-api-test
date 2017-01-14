package org.xyz

import groovy.transform.CompileStatic

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@CompileStatic
class GroovyBean implements Runnable {
    @Override
    void run() {
        println 'Hello from Groovy class!'
    }

}
