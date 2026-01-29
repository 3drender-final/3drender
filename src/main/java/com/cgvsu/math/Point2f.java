package com.cgvsu.math;

public class Point2f {
    public float x;
    public float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2f() {
        this(0, 0);
    }

    public Point2f(Point2f other) {
        this.x = other.x;
        this.y = other.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point2f point2f = (Point2f) obj;
        final float EPS = 1e-6f;
        return Math.abs(x - point2f.x) < EPS && Math.abs(y - point2f.y) < EPS;
    }

    @Override
    public String toString() {
        return "Point2f(" + x + ", " + y + ")";
    }
}
