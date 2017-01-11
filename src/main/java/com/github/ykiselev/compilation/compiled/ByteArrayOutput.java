package com.github.ykiselev.compilation.compiled;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteArrayOutput extends SimpleJavaFileObject implements WritableJavaFileObject {

    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    public ByteArrayOutput(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return os;
    }

    @Override
    public ByteBuffer get() {
        return ByteBuffer.wrap(os.toByteArray());
    }
}
