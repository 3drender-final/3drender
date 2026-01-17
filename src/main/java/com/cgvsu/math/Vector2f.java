package com.cgvsu.math;

public class Vector2f {
    public float x;
    public float y;

    private static final float EPSILON = 1e-7f;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f(Vector2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(x + other.x, y + other.y);
    }

    public Vector2f subtract(Vector2f other) {
        return new Vector2f(x - other.x, y - other.y);
    }

    public Vector2f multiply(float scalar) {
        return new Vector2f(x * scalar, y * scalar);
    }

    public Vector2f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Division by zero");
        return new Vector2f(x / scalar, y / scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector2f normalize() {
        float len = length();
        if (len == 0) return new Vector2f(0, 0);
        return new Vector2f(x / len, y / len);
    }

    public float dot(Vector2f other) {
        return x * other.x + y * other.y;
    }

    public boolean equals(Vector2f other) {
        return Math.abs(x - other.x) < EPSILON &&
               Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2f other = (Vector2f) obj;
        return equals(other);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return "[x = " + x + ", y = " + y + "]";
    }
}
