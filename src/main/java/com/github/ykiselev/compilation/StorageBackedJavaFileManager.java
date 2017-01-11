package com.github.ykiselev.compilation;

import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.HasBinaryName;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
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
        logger.trace("Requested listing {} for {} : {}, recurse? {}", location, packageName, kinds, recurse);
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
        logger.trace("binary name required {} for {}", location, file);
        if (file instanceof HasBinaryName) {
            return ((HasBinaryName) file).binaryName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        logger.debug("Requested {} for {} : {}, {}", location, kind, className, sibling);
        final JavaFileObject fileObject = classStorage.create(location, className, kind, sibling);
        logger.debug("Created {}", fileObject);
        return fileObject;
    }

}
