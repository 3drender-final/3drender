package com.cgvsu.math;

public class Vector4f {
    public float x, y, z, w;

    public Vector4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f(Vector3f v, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector4f vector4f = (Vector4f) obj;
        final float EPS = 1e-6f;
        return Math.abs(x - vector4f.x) < EPS &&
               Math.abs(y - vector4f.y) < EPS &&
               Math.abs(z - vector4f.z) < EPS &&
               Math.abs(w - vector4f.w) < EPS;
    }

    @Override
    public String toString() {
        return "Vector4f(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
