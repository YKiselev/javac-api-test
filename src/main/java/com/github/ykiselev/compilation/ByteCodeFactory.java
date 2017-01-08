package com.github.ykiselev.compilation;

import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface ByteCodeFactory {

    ByteBuffer get(String className);
}
