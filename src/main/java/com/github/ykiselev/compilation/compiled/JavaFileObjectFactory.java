package com.github.ykiselev.compilation.compiled;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface JavaFileObjectFactory {

    WritableJavaFileObject create(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException;

    /**
     * Default implementation producing byte array backed file objects.
     */
    final class Default implements JavaFileObjectFactory {

        @Override
        public WritableJavaFileObject create(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return new ByteArrayOutput(
                    URI.create("bytes:///" + className),
                    kind
            );
        }
    }
}
