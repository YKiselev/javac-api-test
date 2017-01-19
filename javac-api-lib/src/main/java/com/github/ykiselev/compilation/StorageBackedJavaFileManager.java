package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.HasBinaryName;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.google.common.collect.Iterables;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class StorageBackedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ClassStorage classStorage;

    private final SourceStorage sourceStorage;

    private final Charset charset;

    public StorageBackedJavaFileManager(JavaFileManager fileManager, SourceStorage sourceStorage, ClassStorage classStorage, Charset charset) {
        super(fileManager);
        this.sourceStorage = Objects.requireNonNull(sourceStorage);
        this.classStorage = Objects.requireNonNull(classStorage);
        this.charset = Objects.requireNonNull(charset);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classStorage.classLoader();
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        logger.trace("Requested listing {} for {} : {}, recurse? {}", location, packageName, kinds, recurse);
        return Iterables.concat(
                super.list(location, packageName, kinds, recurse),
                kinds.contains(JavaFileObject.Kind.SOURCE)
                        ? list(packageName, recurse)
                        : Collections.emptyList()
        );
    }

    private Iterable<JavaFileObject> list(String packageName, boolean recurse) throws IOException {
        return sourceStorage.list(packageName, recurse)
                .stream()
                .map(SourceFileObject::new)
                .collect(Collectors.toList());
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        logger.trace("binary name required {} for {}", location, file);
        if (file instanceof HasBinaryName) {
            return ((HasBinaryName) file).binaryName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        // Not expected to be called
        throw new UnsupportedOperationException("Not supported!");
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        logger.debug("Requested {} for {} : {}, {}", location, kind, className, sibling);
        final JavaFileObject fileObject = classStorage.create(location, className, kind, sibling);
        logger.debug("Created {}", fileObject);
        return fileObject;
    }

    /**
     *
     */
    private class SourceFileObject extends SimpleJavaFileObject implements HasBinaryName {

        SourceFileObject(String path) {
            // URI i very important here, URI#getPath() should return something, resolvable through SourceStorage#resolve()
            super(URI.create(path.replaceAll("\\\\", "/")), Kind.SOURCE);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return IOUtils.toString(
                    sourceStorage.resolve(getName()),
                    charset
            );
        }

        @Override
        public String binaryName() {
            return FilenameUtils.removeExtension(
                    toUri().getPath()
            ).replace("/", ".");
        }
    }
}
