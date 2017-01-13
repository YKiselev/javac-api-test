package com.github.ykiselev;

import com.github.ykiselev.model.Position;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Positions factory.
 *
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Positions {

    private static final Set<String> GROUPS = ImmutableSet.of("A", "B", "C", "D", "E", "F", "Z");

    private static List<Position> prepare(int count, Iterable<String> types) {
        final Iterator<String> typeIt = Iterables.cycle(types).iterator();
        final List<Position> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(
                    new Position(
                            "p#" + i,
                            Math.PI * i,
                            typeIt.next()
                    )
            );
        }
        return result;
    }

    public static List<Position> prepare(int count) {
        return prepare(count, GROUPS);
    }

}
