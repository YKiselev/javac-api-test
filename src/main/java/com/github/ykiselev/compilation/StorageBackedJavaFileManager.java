package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ByteArrayOutput;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.compilation.source.HasBinaryName;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class StorageBackedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ClassStorage classStorage;

    private final SourceStorage sourceStorage;

    public StorageBackedJavaFileManager(JavaFileManager fileManager, SourceStorage sourceStorage, ClassStorage classStorage) {
        super(fileManager);
        this.sourceStorage = Objects.requireNonNull(sourceStorage);
        this.classStorage = Objects.requireNonNull(classStorage);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classStorage.classLoader();
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        logger.debug("Requested listing {} for {} : {}, recurse? {}", location, packageName, kinds, recurse);
        final List<JavaFileObject> list = Lists.newArrayList(
                super.list(location, packageName, kinds, recurse)
        );
        Iterables.addAll(
                list,
                sourceStorage.list(packageName, recurse)
        );
        return list;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        logger.debug("binary name required {} for {}", location, file);
        if (file instanceof HasBinaryName) {
            return ((HasBinaryName) file).binaryName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        logger.debug("Requested {} for {} : {}", location, packageName, relativeName);
        return super.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        logger.debug("Requested {} for {} : {}", location, packageName, relativeName);
        return super.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        logger.debug("Requested {} for {} : {}, {}", location, kind, className);
        return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        logger.debug("Requested {} for {} : {}, {}", location, kind, className, sibling);
        final ByteArrayOutput result = new ByteArrayOutput(
                URI.create("bytes:///" + className),
                kind
        );
        logger.debug("Created {}", result);
        classStorage.put(className, result);
        return result;
    }

}
