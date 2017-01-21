package com.github.ykiselev.console;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.TrackingClassFactory;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.compilation.source.SourceStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import org.apache.commons.io.IOUtils;

import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
final class Compiler {

    private final ClassFactory classFactory;

    private final SourceStorage sourceStorage;

    Compiler(ClassFactory classFactory, SourceStorage sourceStorage) {
        this.classFactory = requireNonNull(classFactory);
        this.sourceStorage = requireNonNull(sourceStorage);
    }

    static Compiler fromPath(Path path) {
        final DiskSourceStorage sourceStorage = new DiskSourceStorage(path);
        return new Compiler(
                new TrackingClassFactory(
                        new ClassFactory.Default(
                                sourceStorage,
                                new OutputStreamWriter(System.out),
                                StandardCharsets.UTF_8
                        )
                ),
                sourceStorage
        );
    }

    JavaFileObject resolve(String className) throws IOException {
        return new StringJavaSource(
                className,
                JavaFileObject.Kind.SOURCE,
                IOUtils.toString(
                        sourceStorage.resolve(
                                className.replace(".", File.separator) + JavaFileObject.Kind.SOURCE.extension
                        ),
                        StandardCharsets.UTF_8
                )
        );
    }

    Set<ClassLoader> getTrackedClassLoaders() {
        if (classFactory instanceof TrackingClassFactory) {
            return ((TrackingClassFactory) classFactory).getTrackedClassLoaders();
        }
        return Collections.emptySet();
    }

    ClassLoader compile(Iterable<JavaFileObject> compilationUnits) throws Exception {
        return classFactory.compile(
                compilationUnits,
                new ClassStorage.Default(
                        getClass().getClassLoader()
                )
        );
    }

}
