package com.github.ykiselev.console;

import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.CompilationException;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.console.CommandProcessor.CommandHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import javax.tools.JavaFileObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final BufferedReader input;

    private ClassFactory classFactory;

    private Path base = Paths.get("./src/main/scripts");

    private List<String> classNames = new ArrayList<>();

    private DiskSourceStorage sourceStorage;

    private final CommandProcessor processor = new CommandProcessor(
            ImmutableMap.<String, CommandHandler>builder()
                    .put("quit", this::onQuit)
                    .put("call", this::onCall)
                    .put("scripts", this::onScripts)
                    .put("className", this::onClassName)
                    .put("help", this::onHelp)
                    .build()
    );

    private void onHelp(String[] args) {
        System.out.println("Supported commands:");
        processor.handlers()
                .keySet()
                .stream()
                .sorted()
                .forEach(cmd -> System.out.println("  " + cmd));
    }

    private void onScripts(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "Need directory!");
        final Path path = Paths.get(args[1]);
        final File file = path.toFile();
        Preconditions.checkArgument(file.exists(), "Non-existing directory: " + path);
        Preconditions.checkArgument(file.isDirectory(), "Not a directory: " + path);
        this.base = path;
        initClassFactory(this.base);
        System.out.println("Scripts directory set to " + base);
    }

    private void onClassName(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "Need at least one class name!");
        classNames.clear();
        Arrays.stream(args).skip(1).forEach(classNames::add);
        System.out.println("classNames set to " + classNames);
    }

    private void onCall(String[] args) throws Exception {
        final List<String> classNames = args.length >= 2
                ? Arrays.asList(args).subList(1, args.length)
                : this.classNames;
        System.out.println("Compiling " + this.classNames + "...");
        final List<JavaFileObject> objects = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            objects.add(sourceStorage.resolve(className));
        }
        final ClassLoader classLoader = classFactory.compile(
                objects,
                new ClassStorage.Default(
                        getClass().getClassLoader()
                )
        );
        for (String className : classNames) {
            System.out.println("Loading class: " + className);
            final Class<?> clazz = classLoader.loadClass(className);
            if (Function.class.isAssignableFrom(clazz)) {
                final Function<String, String> function = Function.class.cast(clazz.newInstance());
                System.out.println(
                        "Result=" + function.apply(
                                Long.toString(System.currentTimeMillis())
                        )
                );
            } else {
                System.out.println("Loaded " + clazz);
            }
        }
    }

    private void onQuit(String[] args) {
        System.out.println("Bye!");
        System.exit(0);
    }

    private void initClassFactory(Path base) {
        this.sourceStorage = new DiskSourceStorage(
                base,
                StandardCharsets.UTF_8
        );
        this.classFactory = new ClassFactory.Default(
                sourceStorage,
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
        onHelp(new String[0]);
        onClassName(new String[]{"", "org.xyz.Foo", "org.xyz.Bar"});

        String line;
        while ((line = input.readLine()) != null) {
            try {
                processor.execute(line);
            } catch (CompilationException ex) {
                System.err.println(ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
