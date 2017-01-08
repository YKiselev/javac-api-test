package com.github.ykiselev.compilation;

import com.github.ykiselev.JavaOutput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class StorageBackedJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ClassStorage<JavaOutput> storage;

    public StorageBackedJavaFileManager(JavaFileManager fileManager, ClassStorage<JavaOutput> storage) {
        super(fileManager);
        this.storage = Objects.requireNonNull(storage);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return storage.classLoader();
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        logger.debug("Requested java file output for {}, {} : {}, {}", location, kind, className, sibling);
        final JavaOutput result = new JavaOutput(
                URI.create("bytes:///" + className.replace('.', '/')),
                kind
        );
        storage.put(className, result);
        return result;
    }

}
