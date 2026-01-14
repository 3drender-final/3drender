package com.cgvsu.math;

public class Vector4f {
    public float x;
    public float y;
    public float z;
    public float w;

    private static final float EPSILON = 1e-7f;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f() {
        this(0, 0, 0, 0);
    }

    public Vector4f(Vector4f other) {
        this(other.x, other.y, other.z, other.w);
    }

    public Vector4f(Vector3f v, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }

    public Vector4f add(Vector4f other) {
        return new Vector4f(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4f subtract(Vector4f other) {
        return new Vector4f(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Vector4f multiply(float scalar) {
        return new Vector4f(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4f divide(float scalar) {
        if (scalar == 0) throw new ArithmeticException("Division by zero");
        return new Vector4f(x / scalar, y / scalar, z / scalar, w / scalar);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vector4f normalize() {
        float len = length();
        if (len == 0) return new Vector4f(0, 0, 0, 0);
        return new Vector4f(x / len, y / len, z / len, w / len);
    }

    public float dot(Vector4f other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public boolean equals(Vector4f other) {
        return Math.abs(x - other.x) < EPSILON &&
               Math.abs(y - other.y) < EPSILON &&
               Math.abs(z - other.z) < EPSILON &&
               Math.abs(w - other.w) < EPSILON;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector4f other = (Vector4f) obj;
        return equals(other);
    }

    @Override
    public int hashCode() {
        int result = Float.hashCode(x);
        result = 31 * result + Float.hashCode(y);
        result = 31 * result + Float.hashCode(z);
        result = 31 * result + Float.hashCode(w);
        return result;
    }

    @Override
    public String toString() {
        return "[x = " + x + ", y = " + y + ", z = " + z + ", w = " + w + "]";
    }
}
