package com.github.ykiselev;

import org.apache.commons.io.IOUtils;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class JavaSource extends SimpleJavaFileObject {

    public JavaSource(URI uri, Kind kind) {
        super(uri, kind);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        try (InputStream is = open(toUri())) {
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        }
    }

    private InputStream open(URI uri) throws IOException {
        if ("classpath".equals(uri.getScheme())) {
            return getClass().getResourceAsStream(uri.getPath().replaceFirst("/", ""));
        }
        return uri.toURL().openStream();
    }
}
