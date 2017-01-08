package com.github.ykiselev;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavaOutput extends SimpleJavaFileObject implements Supplier<ByteBuffer> {

    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    public JavaOutput(URI uri, Kind kind) {
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
