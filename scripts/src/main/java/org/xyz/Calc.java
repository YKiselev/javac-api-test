package org.xyz;

import com.github.ykiselev.model.Component;
import com.github.ykiselev.model.Group;
import com.github.ykiselev.model.Position;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Calc implements Function<Iterable<Position>, Component> {

    private Set<String> badTypes = ImmutableSet.of("Z");

    public Set<String> getBadTypes() {
        return badTypes;
    }

    public void setBadTypes(Set<String> badTypes) {
        this.badTypes = badTypes;
    }

    @Override
    public Component apply(Iterable<Position> positions) {
        final List<Group> groups = StreamSupport.stream(positions.spliterator(), false)
                .filter(p -> !badTypes.contains(p.getType()))
                .collect(Collectors.groupingBy(Position::getType))
                .entrySet()
                .stream()
                .map(e -> new Group(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new Component(
                groups.stream()
                        .map(Group::getName)
                        .collect(Collectors.joining("|")),
                groups
        );
    }

}
