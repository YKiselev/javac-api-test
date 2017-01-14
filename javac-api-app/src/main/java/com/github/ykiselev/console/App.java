package com.github.ykiselev.console;

import com.github.ykiselev.AnyObject;
import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.CompilationException;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import com.github.ykiselev.console.CommandProcessor.CommandHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

    private final BufferedReader input;

    private ClassFactory classFactory;

    private DiskSourceStorage sourceStorage;

    private String lastLine;

    private String deferredLine;

    private final CommandProcessor processor = new CommandProcessor(
            ImmutableMap.<String, CommandHandler>builder()
                    .put("quit", this::onQuit)
                    .put("base", this::onBase)
                    .put("run", this::onRun)
                    .put("repeat", this::onRepeat)
                    .put("test", this::onTest)
                    .put("help", this::onHelp)
                    .build(),
            this::onEval
    );

    private void onTest(String[] args) {
        Preconditions.checkArgument(args.length == 2, "Need class name!");
        try {
            final Class<?> clazz = getClass().getClassLoader().loadClass(args[1]);
            System.out.println("Found " + clazz + ", loaded by  " + clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
    }

    private void onEval(String[] args) throws Exception {
        Preconditions.checkArgument(args.length >= 1, "Need some expression to evaluate!");
        final String expression = Arrays.stream(args)
                .collect(Collectors.joining(" "));
        final String template = IOUtils.toString(
                getClass().getResourceAsStream("/runnable.template"),
                StandardCharsets.UTF_8
        );
        final String className = "EvaluatedScript";
        final String source = template.replace("${expression}", expression);
        final ClassLoader classLoader = compile(
                Collections.singletonList(
                        new StringJavaSource(
                                URI.create("bytes:///" + className + ".java"),
                                JavaFileObject.Kind.SOURCE,
                                source
                        )
                )
        );
        runClass(
                classLoader.loadClass(className)
        );
    }

    private void onRepeat(String[] args) throws Exception {
        if (StringUtils.isNotEmpty(lastLine)) {
            deferredLine = lastLine;
        } else {
            System.out.println("There is nothing to repeat!");
        }
    }

    private void onHelp(String[] args) {
        System.out.println("Supported commands:");
        processor.handlers()
                .keySet()
                .stream()
                .sorted()
                .forEach(cmd -> System.out.println("  " + cmd));
    }

    private void onBase(String[] args) {
        Preconditions.checkArgument(args.length >= 2, "Need directory!");
        final Path path = Paths.get(args[1]);
        final File file = path.toFile();
        Preconditions.checkArgument(file.exists(), "Non-existing path: " + path);
        Preconditions.checkArgument(file.isDirectory(), "Not a directory: " + path);
        this.sourceStorage = new DiskSourceStorage(
                path,
                StandardCharsets.UTF_8
        );
        this.classFactory = new ClassFactory.Default(
                sourceStorage,
                new OutputStreamWriter(System.out)
        );
        System.out.println("Scripts directory set to " + path);
    }

    private void runClass(Class<?> clazz) throws Exception {
        new AnyObject(clazz.newInstance())
                .run();
    }

    private void onRun(String[] args) throws Exception {
        Preconditions.checkArgument(args.length >= 2, "Need at least one class name!");
        final List<String> classNames = Arrays.stream(args)
                .skip(1)
                .collect(Collectors.toList());
        System.out.println("Compiling " + classNames + "...");
        final List<JavaFileObject> objects = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            objects.add(sourceStorage.resolve(className));
        }
        final ClassLoader classLoader = compile(objects);
        System.out.println("Using class loader " + classLoader);
        for (String className : classNames) {
            System.out.println("Loading class: " + className);
            runClass(
                    classLoader.loadClass(className)
            );
        }
    }

    private ClassLoader compile(Iterable<JavaFileObject> compilationUnits) throws Exception {
        return classFactory.compile(
                compilationUnits,
                new ClassStorage.Default(
                        getClass().getClassLoader()
                )
        );
    }

    private void onQuit(String[] args) {
        System.out.println("Bye!");
        System.exit(0);
    }

    private App(BufferedReader input) {
        this.input = Objects.requireNonNull(input);
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
        onBase(new String[]{"", "../scripts/src/main/java"});

        String line;
        while ((line = input.readLine()) != null) {
            try {
                if (!StringUtils.equals("repeat", line)) {
                    lastLine = line;
                }
                processor.execute(line);
                if (deferredLine != null) {
                    try {
                        processor.execute(deferredLine);
                    } finally {
                        deferredLine = null;
                    }
                }
            } catch (CompilationException ex) {
                System.err.println(ex.getMessage());
            } catch (IllegalArgumentException ex) {
                System.err.println(ex);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
