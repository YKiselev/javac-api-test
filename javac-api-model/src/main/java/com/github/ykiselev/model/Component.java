package com.github.ykiselev.model;

import java.util.List;
import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Component {

    private final String name;

    private final List<Group> groups;

    public String name() {
        return name;
    }

    public List<Group> groups() {
        return groups;
    }

    public Component(String name, List<Group> groups) {
        this.name = name;
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "Component{" +
                "name='" + name + '\'' +
                ", groups=" + groups +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(name, component.name) &&
                Objects.equals(groups, component.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, groups);
    }
}
