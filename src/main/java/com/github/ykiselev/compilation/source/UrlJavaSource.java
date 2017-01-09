package com.github.ykiselev.compilation.source;

import org.apache.commons.io.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class UrlJavaSource extends SimpleJavaFileObject {

    public UrlJavaSource(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        try (InputStream is = open(toUri())) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    private InputStream open(URI uri) throws IOException {
        return uri.toURL().openStream();
    }
}
