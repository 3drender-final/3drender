package com.cgvsu.math;

public class Vector3f {
    public float x;
    public float y;
    public float z;

    private static final float EPSILON = 1e-7f;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f() {
        this(0, 0, 0);
    }

    public Vector3f(Vector3f other) {
        this(other.x, other.y, other.z);
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(x + other.x, y + other.y, z + other.z);
    }

    public Vector3f subtract(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
    }

    public Vector3f multiply(float scalar) {
        return new Vector3f(x * scalar, y * scalar, z * scalar);
    }

    public Vector3f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Division by zero");
        return new Vector3f(x / scalar, y / scalar, z / scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f normalize() {
        float len = length();
        if (len == 0) return new Vector3f(0, 0, 0);
        return new Vector3f(x / len, y / len, z / len);
    }

    public float dot(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector3f cross(Vector3f other) {
        return new Vector3f(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    public boolean equals(Vector3f other) {
        return Math.abs(x - other.x) < EPSILON &&
               Math.abs(y - other.y) < EPSILON &&
               Math.abs(z - other.z) < EPSILON;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3f other = (Vector3f) obj;
        return equals(other);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        result = 31 * result + Float.hashCode(z);
        return result;
    }

    @Override
    public String toString() {
        return "[x = " + x + ", y = " + y + ", z = " + z + "]";
    }
}
