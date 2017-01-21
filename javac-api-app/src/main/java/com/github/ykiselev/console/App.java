package com.github.ykiselev.console;

import com.github.ykiselev.AnyObject;
import com.github.ykiselev.compilation.ClassFactory;
import com.github.ykiselev.compilation.CompilationException;
import com.github.ykiselev.compilation.TrackingClassFactory;
import com.github.ykiselev.compilation.compiled.ClassStorage;
import com.github.ykiselev.compilation.source.DiskSourceStorage;
import com.github.ykiselev.compilation.source.StringJavaSource;
import com.github.ykiselev.console.CommandProcessor.CommandHandler;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class App {

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
                    .put("r", this::onRepeat)
                    .put("test", this::onTest)
                    .put("gc", this::onGc)
                    .put("help", this::onHelp)
                    .build(),
            null
    );

    private Set<ClassLoader> getTrackedClassLoaders() {
        if (classFactory instanceof TrackingClassFactory) {
            return ((TrackingClassFactory) classFactory).getTrackedClassLoaders();
        }
        return Collections.emptySet();
    }

    private int printTrackedClassLoaders() {
        final Set<ClassLoader> classLoaders = getTrackedClassLoaders();
        if (classLoaders.isEmpty()) {
            System.out.println("No class loaders tracked.");
        } else {
            System.out.println("Tracked class loaders:");
            for (ClassLoader classLoader : classLoaders) {
                System.out.println("  " + classLoader);
            }
        }
        return classLoaders.size();
    }

    private void onGc(String[] args) {
        final long fmBefore = Runtime.getRuntime().freeMemory() / 1024;
        final int before = printTrackedClassLoaders();
        System.gc();
        final long fmAfter = Runtime.getRuntime().freeMemory() / 1024;
        if (before > 0) {
            printTrackedClassLoaders();
        }
        System.out.println("Free memory before: " + fmBefore + " KB, after: " + fmAfter + " KB, delta: +" + Math.max(0, fmAfter - fmBefore) + " KB");
    }

    private void onTest(String[] args) {
        Preconditions.checkArgument(args.length == 2, "Need class name!");
        try {
            final Class<?> clazz = getClass().getClassLoader().loadClass(args[1]);
            System.out.println("Found " + clazz + ", loaded by  " + clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }
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
        this.sourceStorage = new DiskSourceStorage(path);
        this.classFactory = new TrackingClassFactory(
                new ClassFactory.Default(
                        sourceStorage,
                        new OutputStreamWriter(System.out),
                        StandardCharsets.UTF_8
                )
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
            objects.add(
                    new StringJavaSource(
                            className,
                            Kind.SOURCE,
                            IOUtils.toString(
                                    sourceStorage.resolve(
                                            className.replace(".", File.separator) + Kind.SOURCE.extension
                                    ),
                                    StandardCharsets.UTF_8
                            )
                    )
            );
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

    public static void main(String[] args) throws IOException {
        new App().run();
    }

    private void run() throws IOException {
        onHelp(new String[0]);
        onBase(new String[]{"", "../scripts/src/main/java"});

        final BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in)
        );
        String line;
        while ((line = input.readLine()) != null) {
            try {
                if (!line.equals("repeat") && !line.equals("r")) {
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
