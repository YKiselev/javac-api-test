package com.github.ykiselev.model;

import java.util.Objects;

/**
 * @author Yuriy Kiselev (uze@yandex.ru).
 */
public final class Position {

    private final String name;

    private final double value;

    private final String type;

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Position(String name, double value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Position{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.value, value) == 0 &&
                Objects.equals(name, position.name) &&
                Objects.equals(type, position.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, type);
    }
}
