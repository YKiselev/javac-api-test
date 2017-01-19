package com.github.ykiselev.compilation.source;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class StringJavaSource extends SimpleJavaFileObject {

    private final String content;

    public StringJavaSource(String className, Kind kind, String content) {
        super(URI.create("string:///" + className.replaceAll("\\.", "/") + kind.extension), kind);
        this.content = Objects.requireNonNull(content);
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}
