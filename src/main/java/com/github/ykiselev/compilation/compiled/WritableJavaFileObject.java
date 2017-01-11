package com.github.ykiselev.compilation.compiled;

import javax.tools.JavaFileObject;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface WritableJavaFileObject extends JavaFileObject, Supplier<ByteBuffer> {

}
