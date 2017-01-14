package org.xyz;

import com.github.ykiselev.model.Component;
import com.github.ykiselev.model.Group;
import com.github.ykiselev.model.Position;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Calc implements Function<Iterable<Position>, Component> {

    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public Component apply(Iterable<Position> positions) {
        final List<Group> groups = StreamSupport.stream(positions.spliterator(), false)
                .filter(p -> !StringUtils.equals(p.getType(), "A"))
                .collect(Collectors.groupingBy(Position::getType))
                .entrySet()
                .stream()
                .map(e -> new Group(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new Component(
                groups.stream()
                        .map(Group::getName)
                        .collect(Collectors.joining("+")),
                groups
        );
    }

}
