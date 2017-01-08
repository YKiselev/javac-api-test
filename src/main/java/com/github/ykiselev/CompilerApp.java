package com.github.ykiselev;

import com.github.ykiselev.compilation.ClassStorage;
import com.github.ykiselev.compilation.JavaSource;
import com.github.ykiselev.compilation.StorageBackedJavaFileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.tools.*;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class CompilerApp {

    private final Logger logger = LogManager.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        new CompilerApp().run();
    }

    private JavaSource source(String path) {
        return new JavaSource(
                new File(path).toURI(),
                JavaFileObject.Kind.SOURCE
        );
    }

    private void run() throws Exception {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final StringWriter out = new StringWriter();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final Boolean result;
        final ClassStorage.Default storage = new ClassStorage.Default(getClass().getClassLoader());
        try (
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(
                        this::report,
                        Locale.getDefault(),
                        StandardCharsets.UTF_8
                );
                StorageBackedJavaFileManager forwardingFileManager = new StorageBackedJavaFileManager(
                        fileManager,
                        storage
                )
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
                    Arrays.asList(
                            source("src/main/scripts/org/xyz/Foo.java"),
                            source("src/main/scripts/org/xyz/Bar.java")
                    )
            );
            result = task.call();
        }

        final String output = out.toString();
        if (!output.isEmpty()) {
            logger.info(output);
        }
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            report(diagnostic);
        }

        if (result != Boolean.TRUE) {
            logger.error("Compilation failed!");
        } else {
            logger.info("Compilation successful!");

            final ClassLoader classLoader = storage.classLoader();
            final Class<?> clazz = classLoader.loadClass("org.xyz.Foo");
            final Function<String, Double> s2d = Function.class.cast(clazz.newInstance());
            logger.info("Loaded {}, '123.456'={}", clazz, s2d.apply("123.456"));
        }
    }

    private void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        logger.info(diagnostic.getCode());
        logger.info(diagnostic.getKind());
        logger.info(diagnostic.getPosition());
        logger.info(diagnostic.getStartPosition());
        logger.info(diagnostic.getEndPosition());
        logger.info(diagnostic.getSource());
        logger.info(diagnostic.getMessage(Locale.getDefault()));
    }
}
