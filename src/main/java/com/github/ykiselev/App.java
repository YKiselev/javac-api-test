package com.github.ykiselev;

import com.github.ykiselev.compilation.InMemoryJavaFileManager;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Function;

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
        final ClassLoader classLoader;
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                this::report,
                Locale.getDefault(),
                StandardCharsets.UTF_8
        );
             InMemoryJavaFileManager forwardingFileManager = new InMemoryJavaFileManager(fileManager)
        ) {
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
                                    new File("src/main/scripts/org/xyz/Foo.java").toURI(),
                                    JavaFileObject.Kind.SOURCE
                            )
                    )
            );
            result = task.call();
            classLoader = forwardingFileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
        }
        System.out.println(out.toString());
        if (result != Boolean.TRUE) {
            System.out.println("Compilation failed!");
        } else {
            System.out.println("Compilation successful!");
            final Class<?> clazz = classLoader.loadClass("org.xyz.Foo");
            final Function<String,Double> s2d = Function.class.cast(clazz.newInstance());
            System.out.println("Loaded class" + clazz +", '123.456'="+s2d.apply("123.456"));
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
