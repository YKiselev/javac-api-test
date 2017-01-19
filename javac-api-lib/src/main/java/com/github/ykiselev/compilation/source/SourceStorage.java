package com.github.ykiselev.compilation.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public interface SourceStorage {

    /**
     * Returns list of objects representing files in specified package (and in sub packages if {@code recurse} is {@code true}.
     *
     * @param packageName the name of package to list files in
     * @param recurse     if true - list sub-packages as well
     * @return file names
     * @throws IOException if I/O error occurred
     */
    Collection<String> list(String packageName, boolean recurse) throws IOException;

    /**
     * Resolves single file.
     *
     * @param fileName the name of file
     * @return stream to read file content
     * @throws IOException if I/O error occurred
     */
    InputStream resolve(String fileName) throws IOException;

}
