package com.github.ykiselev;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavaOutput extends SimpleJavaFileObject {

    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    public JavaOutput(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return os;
    }

    public byte[] toByteArray() {
        return os.toByteArray();
    }
}
