package com.github.ykiselev.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Group {

    private final String name;

    private final List<Position> positions;

    public String getName() {
        return name;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public Group(String name, List<Position> positions) {
        this.name = name;
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", positions=" + positions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name) &&
                Objects.equals(positions, group.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, positions);
    }
}
