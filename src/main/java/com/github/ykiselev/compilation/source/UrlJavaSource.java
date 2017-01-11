package com.github.ykiselev.compilation.source;

import org.apache.commons.io.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class UrlJavaSource extends SimpleJavaFileObject {

    private final Charset charset;

    public UrlJavaSource(URI uri, Kind kind, Charset charset) {
        super(uri, kind);
        this.charset = Objects.requireNonNull(charset);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        try (InputStream is = toUri().toURL().openStream()) {
            return IOUtils.toString(is, charset);
        }
    }

}
