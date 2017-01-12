package com.github.ykiselev.compilation.compiled;

import com.google.common.base.Preconditions;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class ByteArrayOutput extends SimpleJavaFileObject implements JavaFileObject, Supplier<ByteBuffer> {

    private AtomicReference<ByteBuffer> buffer = new AtomicReference<>();

    public ByteArrayOutput(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                ByteArrayOutput.this.flush(toByteArray());
            }
        };
    }

    private void flush(byte[] byteCode) {
        Preconditions.checkArgument(
                buffer.compareAndSet(null, ByteBuffer.wrap(byteCode)),
                "Buffer already set!"
        );
    }

    @Override
    public ByteBuffer get() {
        return buffer.get();
    }
}
