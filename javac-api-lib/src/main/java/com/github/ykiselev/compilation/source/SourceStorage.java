package com.github.ykiselev.compilation.source;

import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SourceStorage {

    /**
     * Returns list of objects representing files in specified package (and in sub packages if {@code recurse} is {@code true}.
     *
     * @param packageName the name of package to list files in
     * @param recurse     if true - list sub-packages as well
     * @return list of {@link JavaFileObject}'s
     * @throws IOException is I/O error occurred
     */
    Iterable<JavaFileObject> list(String packageName, boolean recurse) throws IOException;

    /**
     * Resolves single file.
     *
     * @param fileName the name of file
     * @return the non-null file object (will be invalid if not found)
     * @throws IOException if I/O error occurred
     */
    JavaFileObject resolve(String fileName) throws IOException;

}
