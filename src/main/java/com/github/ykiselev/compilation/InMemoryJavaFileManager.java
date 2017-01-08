package com.github.ykiselev.compilation;

import com.github.ykiselev.JavaOutput;

import javax.tools.*;
import java.io.IOException;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class InMemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    private final Map<String, JavaOutput> classes = new ConcurrentHashMap<>();

    public InMemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        System.out.println("Need file object for " + location + ", " + packageName + ", " + relativeName + ", " + sibling);
        return super.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        System.out.println("Need classloader for " + location);
        return new SecureClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                final JavaOutput output = classes.get(name);
                if (output != null) {
                    final byte[] bytes = output.toByteArray();
                    return super.defineClass(name, bytes, 0, bytes.length);
                }
                return super.findClass(name);
            }
        };
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        System.out.println("Need input: " + location + ", " + className + "," + kind);
        return super.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        System.out.println("Need java file output: " + location + ", " + className + "," + kind + ", " + sibling);
        if (location == StandardLocation.CLASS_OUTPUT) {
            final JavaOutput result = new JavaOutput(URI.create("bytes:///" + className.replace('.', '/')), kind);
            classes.put(className, result);
            return result;
        }
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

}
