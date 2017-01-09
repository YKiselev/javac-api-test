package com.github.ykiselev.compilation.compiled;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ByteCodeFactory {

    ByteBuffer get(String className);
}
