package com.github.ykiselev.javac;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.scripting.ScriptSource;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class UriScriptSource implements ScriptSource {

    private final Resource resource;

    private final String className;

    public UriScriptSource(Resource resource, String className) {
        this.resource = Objects.requireNonNull(resource);
        this.className = Objects.requireNonNull(className);
    }

    @Override
    public String getScriptAsString() throws IOException {
        return IOUtils.toString(resource.getInputStream(), "utf-8");
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public String suggestedClassName() {
        return className;
    }
}
