package com.github.ykiselev.compilation.source;

import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SourceStorage {

    Iterable<JavaFileObject> list(String packageName, boolean recurse) throws IOException;

    JavaFileObject resolve(String className) throws IOException;

    String inferBinaryName(JavaFileObject object);
}
