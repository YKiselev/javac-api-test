import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
@Deprecated
@Ignore
public final class PathTest {

    @Test
    public void shouldList() throws Exception {
        try (Stream<Path> stream = Files.find(Paths.get("D:\\Downloads4"), 1, (p, a) -> a.isRegularFile())) {
            stream.forEach(System.out::println);
        }
    }
}
