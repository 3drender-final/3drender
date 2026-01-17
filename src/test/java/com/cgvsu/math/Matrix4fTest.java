package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Matrix4fTest {

    private static final float EPSILON = 1e-7f;

    @Test
    public void testDefaultConstructor() {
        Matrix4f m = new Matrix4f();

        assertEquals(1.0f, m.get(0, 0), EPSILON);
        assertEquals(0.0f, m.get(0, 1), EPSILON);
        assertEquals(0.0f, m.get(0, 2), EPSILON);
        assertEquals(0.0f, m.get(0, 3), EPSILON);
        assertEquals(0.0f, m.get(1, 0), EPSILON);
        assertEquals(1.0f, m.get(1, 1), EPSILON);
        assertEquals(0.0f, m.get(1, 2), EPSILON);
        assertEquals(0.0f, m.get(1, 3), EPSILON);
        assertEquals(0.0f, m.get(2, 0), EPSILON);
        assertEquals(0.0f, m.get(2, 1), EPSILON);
        assertEquals(1.0f, m.get(2, 2), EPSILON);
        assertEquals(0.0f, m.get(2, 3), EPSILON);
        assertEquals(0.0f, m.get(3, 0), EPSILON);
        assertEquals(0.0f, m.get(3, 1), EPSILON);
        assertEquals(0.0f, m.get(3, 2), EPSILON);
        assertEquals(1.0f, m.get(3, 3), EPSILON);
    }

    @Test
    public void testArrayConstructor() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m = new Matrix4f(data);

        assertEquals(1.0f, m.get(0, 0), EPSILON);
        assertEquals(2.0f, m.get(0, 1), EPSILON);
        assertEquals(3.0f, m.get(0, 2), EPSILON);
        assertEquals(4.0f, m.get(0, 3), EPSILON);
        assertEquals(5.0f, m.get(1, 0), EPSILON);
        assertEquals(6.0f, m.get(1, 1), EPSILON);
        assertEquals(7.0f, m.get(1, 2), EPSILON);
        assertEquals(8.0f, m.get(1, 3), EPSILON);
        assertEquals(9.0f, m.get(2, 0), EPSILON);
        assertEquals(10.0f, m.get(2, 1), EPSILON);
        assertEquals(11.0f, m.get(2, 2), EPSILON);
        assertEquals(12.0f, m.get(2, 3), EPSILON);
        assertEquals(13.0f, m.get(3, 0), EPSILON);
        assertEquals(14.0f, m.get(3, 1), EPSILON);
        assertEquals(15.0f, m.get(3, 2), EPSILON);
        assertEquals(16.0f, m.get(3, 3), EPSILON);
    }

    @Test
    public void testSetIdentity() {
        Matrix4f m = new Matrix4f();
        m.setZero();
        m.setIdentity();

        assertEquals(1.0f, m.get(0, 0), EPSILON);
        assertEquals(0.0f, m.get(0, 1), EPSILON);
        assertEquals(0.0f, m.get(0, 2), EPSILON);
        assertEquals(0.0f, m.get(0, 3), EPSILON);
        assertEquals(0.0f, m.get(1, 0), EPSILON);
        assertEquals(1.0f, m.get(1, 1), EPSILON);
        assertEquals(0.0f, m.get(1, 2), EPSILON);
        assertEquals(0.0f, m.get(1, 3), EPSILON);
        assertEquals(0.0f, m.get(2, 0), EPSILON);
        assertEquals(0.0f, m.get(2, 1), EPSILON);
        assertEquals(1.0f, m.get(2, 2), EPSILON);
        assertEquals(0.0f, m.get(2, 3), EPSILON);
        assertEquals(0.0f, m.get(3, 0), EPSILON);
        assertEquals(0.0f, m.get(3, 1), EPSILON);
        assertEquals(0.0f, m.get(3, 2), EPSILON);
        assertEquals(1.0f, m.get(3, 3), EPSILON);
    }

    @Test
    public void testSetZero() {
        Matrix4f m = new Matrix4f();
        m.setZero();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0f, m.get(i, j), EPSILON);
            }
        }
    }

    @Test
    public void testAdd() {
        float[][] data1 = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        float[][] data2 = {
                {1, 1, 1, 1},
                {2, 2, 2, 2},
                {1, 1, 1, 1},
                {2, 2, 2, 2}
        };

        Matrix4f m1 = new Matrix4f(data1);
        Matrix4f m2 = new Matrix4f(data2);
        Matrix4f result = m1.add(m2);

        assertEquals(2.0f, result.get(0, 0), EPSILON);
        assertEquals(3.0f, result.get(0, 1), EPSILON);
        assertEquals(4.0f, result.get(0, 2), EPSILON);
        assertEquals(5.0f, result.get(0, 3), EPSILON);
        assertEquals(7.0f, result.get(1, 0), EPSILON);
        assertEquals(8.0f, result.get(1, 1), EPSILON);
        assertEquals(9.0f, result.get(1, 2), EPSILON);
        assertEquals(10.0f, result.get(1, 3), EPSILON);
        assertEquals(10.0f, result.get(2, 0), EPSILON);
        assertEquals(11.0f, result.get(2, 1), EPSILON);
        assertEquals(12.0f, result.get(2, 2), EPSILON);
        assertEquals(13.0f, result.get(2, 3), EPSILON);
        assertEquals(15.0f, result.get(3, 0), EPSILON);
        assertEquals(16.0f, result.get(3, 1), EPSILON);
        assertEquals(17.0f, result.get(3, 2), EPSILON);
        assertEquals(18.0f, result.get(3, 3), EPSILON);
    }

    @Test
    public void testSubtract() {
        float[][] data1 = {
                {10, 10, 10, 10},
                {10, 10, 10, 10},
                {10, 10, 10, 10},
                {10, 10, 10, 10}
        };
        float[][] data2 = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m1 = new Matrix4f(data1);
        Matrix4f m2 = new Matrix4f(data2);
        Matrix4f result = m1.subtract(m2);

        assertEquals(9.0f, result.get(0, 0), EPSILON);
        assertEquals(8.0f, result.get(0, 1), EPSILON);
        assertEquals(7.0f, result.get(0, 2), EPSILON);
        assertEquals(6.0f, result.get(0, 3), EPSILON);
        assertEquals(5.0f, result.get(1, 0), EPSILON);
        assertEquals(4.0f, result.get(1, 1), EPSILON);
        assertEquals(3.0f, result.get(1, 2), EPSILON);
        assertEquals(2.0f, result.get(1, 3), EPSILON);
        assertEquals(1.0f, result.get(2, 0), EPSILON);
        assertEquals(0.0f, result.get(2, 1), EPSILON);
        assertEquals(-1.0f, result.get(2, 2), EPSILON);
        assertEquals(-2.0f, result.get(2, 3), EPSILON);
        assertEquals(-3.0f, result.get(3, 0), EPSILON);
        assertEquals(-4.0f, result.get(3, 1), EPSILON);
        assertEquals(-5.0f, result.get(3, 2), EPSILON);
        assertEquals(-6.0f, result.get(3, 3), EPSILON);
    }

    @Test
    public void testMultiplyVec() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f m = new Matrix4f(data);
        Vector4f v = new Vector4f(2, 3, 4, 5);

        Vector4f result = m.multiplyVec(v);

        assertEquals(40.0f, result.x, EPSILON);
        assertEquals(96.0f, result.y, EPSILON);
        assertEquals(152.0f, result.z, EPSILON);
        assertEquals(208.0f, result.w, EPSILON);
    }

    @Test
    public void testMultiplyZeroVec() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f m = new Matrix4f(data);
        Vector4f v = new Vector4f(0, 0, 0, 0);

        Vector4f result = m.multiplyVec(v);

        assertEquals(0.0f, result.x, EPSILON);
        assertEquals(0.0f, result.y, EPSILON);
        assertEquals(0.0f, result.z, EPSILON);
        assertEquals(0.0f, result.w, EPSILON);
    }

    @Test
    public void testMultiply() {
        float[][] data1 = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        float[][] data2 = {
                {2, 2, 2, 2},
                {2, 2, 2, 2},
                {2, 2, 2, 2},
                {2, 2, 2, 2}
        };

        Matrix4f m1 = new Matrix4f(data1);
        Matrix4f m2 = new Matrix4f(data2);
        Matrix4f result = m1.multiply(m2);

        assertEquals(20.0f, result.get(0, 0), EPSILON);
        assertEquals(20.0f, result.get(0, 1), EPSILON);
        assertEquals(20.0f, result.get(0, 2), EPSILON);
        assertEquals(20.0f, result.get(0, 3), EPSILON);

        assertEquals(52.0f, result.get(1, 0), EPSILON);
        assertEquals(52.0f, result.get(1, 1), EPSILON);
        assertEquals(52.0f, result.get(1, 2), EPSILON);
        assertEquals(52.0f, result.get(1, 3), EPSILON);

        assertEquals(84.0f, result.get(2, 0), EPSILON);
        assertEquals(84.0f, result.get(2, 1), EPSILON);
        assertEquals(84.0f, result.get(2, 2), EPSILON);
        assertEquals(84.0f, result.get(2, 3), EPSILON);

        assertEquals(116.0f, result.get(3, 0), EPSILON);
        assertEquals(116.0f, result.get(3, 1), EPSILON);
        assertEquals(116.0f, result.get(3, 2), EPSILON);
        assertEquals(116.0f, result.get(3, 3), EPSILON);
    }

    @Test
    public void testMultiplyZeroMatrix() {
        Matrix4f zero = new Matrix4f();
        zero.setZero();
        Matrix4f m = new Matrix4f();

        Matrix4f result = m.multiply(zero);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0.0f, result.get(i, j), EPSILON);
            }
        }
    }

    @Test
    public void testTranspose() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m = new Matrix4f(data);
        Matrix4f transposed = m.transpose();

        assertEquals(1.0f, transposed.get(0, 0), EPSILON);
        assertEquals(5.0f, transposed.get(0, 1), EPSILON);
        assertEquals(9.0f, transposed.get(0, 2), EPSILON);
        assertEquals(13.0f, transposed.get(0, 3), EPSILON);
        assertEquals(2.0f, transposed.get(1, 0), EPSILON);
        assertEquals(6.0f, transposed.get(1, 1), EPSILON);
        assertEquals(10.0f, transposed.get(1, 2), EPSILON);
        assertEquals(14.0f, transposed.get(1, 3), EPSILON);
        assertEquals(3.0f, transposed.get(2, 0), EPSILON);
        assertEquals(7.0f, transposed.get(2, 1), EPSILON);
        assertEquals(11.0f, transposed.get(2, 2), EPSILON);
        assertEquals(15.0f, transposed.get(2, 3), EPSILON);
        assertEquals(4.0f, transposed.get(3, 0), EPSILON);
        assertEquals(8.0f, transposed.get(3, 1), EPSILON);
        assertEquals(12.0f, transposed.get(3, 2), EPSILON);
        assertEquals(16.0f, transposed.get(3, 3), EPSILON);
    }

    @Test
    public void testTransposeIdentity() {
        Matrix4f m = new Matrix4f();
        Matrix4f transposed = m.transpose();

        assertEquals(m, transposed);
    }

    @Test
    public void testTransposeTwice() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m = new Matrix4f(data);
        Matrix4f transposed = m.transpose().transpose();

        assertEquals(m, transposed);
    }

    @Test
    public void testEquals() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m1 = new Matrix4f(data);
        Matrix4f m2 = new Matrix4f(data);

        assertTrue(m1.equals(m2));
        assertTrue(m2.equals(m1));
    }

    @Test
    public void testEqualsWithEpsilon() {
        Matrix4f m1 = new Matrix4f();
        float[][] data = {
                {1.0000000001f, 0.0000000001f, 0.0000000001f, 0.0000000001f},
                {0.0000000001f, 1.0000000001f, 0.0000000001f, 0.0000000001f},
                {0.0000000001f, 0.0000000001f, 1.0000000001f, 0.0000000001f},
                {0.0000000001f, 0.0000000001f, 0.0000000001f, 1.0000000001f}
        };

        Matrix4f m2 = new Matrix4f(data);
        assertTrue(m1.equals(m2));
    }

    @Test
    public void testHashCodeConsistency() {
        float[][] data = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        Matrix4f m1 = new Matrix4f(data);
        Matrix4f m2 = new Matrix4f(data);
        Matrix4f m3 = new Matrix4f();

        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1.hashCode(), m3.hashCode());
    }

    @Test
    public void testToString() {
        Matrix4f m = new Matrix4f();
        String str = m.toString();

        assertNotNull(str);
        assertTrue(str.contains("1.00") || str.contains("1,00"));
        assertTrue(str.contains("0.00") || str.contains("0,00"));
        assertTrue(str.contains("["));
        assertTrue(str.contains("]"));
        assertTrue(str.contains("\n"));
    }
}
