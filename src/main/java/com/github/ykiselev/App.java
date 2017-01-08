package com.github.ykiselev;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    public static void main(String[] args) throws Exception {
        new App().run();
    }

    private void run() throws Exception {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StringWriter out = new StringWriter();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final Boolean result;
        final List<JavaOutput> outputs = new ArrayList<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                this::report,
                Locale.getDefault(),
                StandardCharsets.UTF_8
        )) {
            final ForwardingJavaFileManager forwardingFileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(fileManager) {
                @Override
                public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
                    System.out.println("Need file object for " + location + ", " + packageName + ", " + relativeName + ", " + sibling);
                    return super.getFileForOutput(location, packageName, relativeName, sibling);
                }

                @Override
                public ClassLoader getClassLoader(Location location) {
                    System.out.println("Need classloader for " + location);
                    return super.getClassLoader(location);
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
                        final JavaOutput result;
                        try {
                            result = new JavaOutput(new URI("class:///" + className), kind);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        outputs.add(result);
                        return result;
                    }
                    return super.getJavaFileForOutput(location, className, kind, sibling);
                }
            };
            // set output dir
            fileManager.setLocation(
                    StandardLocation.CLASS_OUTPUT,
                    Collections.singletonList(
                            new File("D:\\Downloads\\classes")
                    )
            );
            final JavaCompiler.CompilationTask task = compiler.getTask(
                    out,
                    forwardingFileManager,
                    diagnostics,
                    null,
                    null,
                    Collections.singletonList(
                            new JavaSource(
                                    new File("src/main/java/org/xyz/Foo.java").toURI(),
                                    JavaFileObject.Kind.SOURCE
                            )
                    )
            );
            result = task.call();
        }
        System.out.println(out.toString());
        if (result != Boolean.TRUE) {
            System.out.println("Compilation failed!");
        } else {
            System.out.println("Compilation successful!");
            for (JavaOutput output : outputs) {
                System.out.println("Class file: " + output);
            }
        }
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            report(diagnostic);
        }
    }

    private void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        System.out.println(diagnostic.getCode());
        System.out.println(diagnostic.getKind());
        System.out.println(diagnostic.getPosition());
        System.out.println(diagnostic.getStartPosition());
        System.out.println(diagnostic.getEndPosition());
        System.out.println(diagnostic.getSource());
        System.out.println(diagnostic.getMessage(Locale.getDefault()));
    }
}
