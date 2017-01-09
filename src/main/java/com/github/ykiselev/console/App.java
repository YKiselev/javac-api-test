package com.github.ykiselev.console;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.JavaSource;
import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.console.CommandProcessor.CommandHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final BufferedReader input;

    private ClassFactory classFactory;

    private Path base = Paths.get("./src/main/scripts");

    private String className = "org.xyz.Foo";

    private final CommandProcessor processor = new CommandProcessor(
            ImmutableMap.<String, CommandHandler>builder()
                    .put("quit", this::onQuit)
                    .put("call", this::onCall)
                    .put("scripts", this::onScripts)
                    .put("className", this::onClassName)
                    .build()
    );

    private void onScripts(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "Need directory!");
        final Path path = Paths.get(args[1]);
        final File file = path.toFile();
        Preconditions.checkArgument(file.exists(), "Non-existing directory: " + path);
        Preconditions.checkArgument(file.isDirectory(), "Not a directory: " + path);
        this.base = path;
        initClassFactory(this.base);
    }

    private void onClassName(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "Need class name!");
    }

    private void onCall(String[] args) throws Exception {
        final String className = args.length >= 2 ? args[1] : this.className;
        final ClassLoader classLoader = classFactory.compile(
                Collections.singletonList(
                        source(className)
                )
        );
        final Class<?> clazz = classLoader.loadClass(className);
        final Function<String, String> function = Function.class.cast(clazz.newInstance());
        final String result = function.apply(Long.toString(System.currentTimeMillis()));
        System.out.println("Result=" + result);
    }

    private void onQuit(String[] args) {
        System.out.println("Bye!");
        System.exit(0);
    }

    private void initClassFactory(Path base) {
        this.classFactory = new ClassFactory.Default(
                getClass().getClassLoader(),
                new DiskSourceStorage(base),
                StandardCharsets.UTF_8,
                new OutputStreamWriter(System.out)
        );
    }

    private App(BufferedReader input) {
        this.input = Objects.requireNonNull(input);
        initClassFactory(base);
    }

    public static void main(String[] args) throws IOException {
        new App(
                new BufferedReader(
                        new InputStreamReader(System.in)
                )
        ).run();
    }

    private void run() throws IOException {
        String line;
        while ((line = input.readLine()) != null) {
            try {
                processor.execute(line);
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    private JavaFileObject source(String className) {
        return new JavaSource(
                base.resolve(className.replace(".", "/") + JavaFileObject.Kind.SOURCE.extension).normalize().toUri(),
                JavaFileObject.Kind.SOURCE
        );
    }
}
