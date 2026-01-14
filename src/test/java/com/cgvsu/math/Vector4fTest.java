package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Vector4fTest {
    private static final float EPSILON = 1e-7f;

    @Test
    public void testConstructors() {
        Vector4f v1 = new Vector4f();
        assertEquals(0.0f, v1.x, EPSILON);
        assertEquals(0.0f, v1.y, EPSILON);
        assertEquals(0.0f, v1.z, EPSILON);
        assertEquals(0.0f, v1.w, EPSILON);

        Vector4f v2 = new Vector4f(1.5f, 2.5f,3.5f,4.5f);
        assertEquals(1.5f, v2.x, EPSILON);
        assertEquals(2.5f, v2.y, EPSILON);
        assertEquals(3.5f, v2.z, EPSILON);
        assertEquals(4.5f, v2.w, EPSILON);

        Vector4f v3 = new Vector4f(v2);
        assertEquals(1.5f, v3.x, EPSILON);
        assertEquals(2.5f, v3.y, EPSILON);
        assertEquals(3.5f, v3.z, EPSILON);
        assertEquals(4.5f, v3.w, EPSILON);
    }

    @Test
    public void testAdd() {
        Vector4f v1 = new Vector4f(1,0,1, 0);
        Vector4f v2 = new Vector4f(0,1, 0, 1);
        Vector4f result = new Vector4f(1,1, 1, 1);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx2() {
        Vector4f v1 = new Vector4f(-1,0, 3, 0);
        Vector4f v2 = new Vector4f(-2,-1, -10, 0);
        Vector4f result = new Vector4f(-3,-1, -7, 0);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx3() {
        Vector4f v1 = new Vector4f(1.2f,2.1f, 3.3f, 4.4f);
        Vector4f v2 = new Vector4f(0.8f,1.9f, 3.7f, 4.6f);
        Vector4f result = v1.add(v2);
        assertEquals(2.0f, result.x, EPSILON);
        assertEquals(4.0f, result.y, EPSILON);
        assertEquals(7.0f, result.z, EPSILON);
        assertEquals(9.0f, result.w, EPSILON);
    }

    @Test
    public void testSubtractx1() {
        Vector4f v1 = new Vector4f(3, 4,5,6);
        Vector4f v2 = new Vector4f(0, 1,2,3);
        Vector4f result = new Vector4f(3,3, 3,3);
        assertEquals(result, v1.subtract(v2));
    }

    @Test
    public void testSubtractx2() {
        Vector4f v1 = new Vector4f(2,3, 10,0);
        Vector4f v2 = new Vector4f(2,7, 1,0);
        Vector4f result = new Vector4f(0,-4, 9,0);
        assertEquals(result,v1.subtract(v2));
    }

    @Test
    public void testSubtractx3() {
        Vector4f v1 = new Vector4f(2.0f,3.0f, 4.0f,5.0f);
        Vector4f v2 = new Vector4f(4.5f,3.0f, 5.5f, 6.5f);
        Vector4f result = v1.subtract(v2);
        assertEquals(-2.5f, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(-1.5f, result.z, EPSILON);
        assertEquals(-1.5f, result.w, EPSILON);
    }

    @Test
    public void testMultiplyx1() {
        Vector4f v = new Vector4f(2,3, 4,5);
        float scalar = 0.5f;
        Vector4f result = v.multiply(scalar);
        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(1.5f, result.y, EPSILON);
        assertEquals(2, result.z, EPSILON);
        assertEquals(2.5f, result.w, EPSILON);
    }

    @Test
    public void testMultiplyx2() {
        Vector4f v = new Vector4f(2,3, 4,5);
        float scalar = 0;
        Vector4f result = new Vector4f(0,0, 0,0);
        assertEquals(result, v.multiply(scalar));
    }

    @Test
    public void testMultiplyx3() {
        Vector4f v = new Vector4f(1.1f, -2.2f,3.3f, -4.4f );
        float scalar = -1.1f;
        Vector4f result = v.multiply(scalar);
        assertEquals(-1.21f, result.x, EPSILON);
        assertEquals(2.42f, result.y, EPSILON);
        assertEquals(-3.63f, result.z, EPSILON);
        assertEquals(4.84f, result.w, EPSILON);
    }

    @Test
    public void testDivide() {
        Vector4f v = new Vector4f(2.5f,3, 4.5f,5);
        float scalar = 0.5f;
        Vector4f result = v.divide(scalar);
        assertEquals(5, result.x, EPSILON);
        assertEquals(6, result.y, EPSILON);
        assertEquals(9, result.z, EPSILON);
        assertEquals(10, result.w, EPSILON);
    }

    @Test
    public void testDivideByZero() {
        Vector4f v = new Vector4f(2.5f,3, 4.5f, 5);
        float scalar = 0;
        try {
            v.divide(scalar);
            fail("Exception expected");
        } catch (ArithmeticException e) {
            assertTrue(e.getMessage().contains("Division by zero"));
        }
    }

    @Test
    public void testDivideByEpsilon() {
        Vector4f v = new Vector4f(2.5f,3,4.5f, 5);
        try {
            Vector4f result = v.divide(EPSILON);
            assertTrue(result.x > 0);
            assertTrue(result.y > 0);
            assertTrue(result.z > 0);
            assertTrue(result.w > 0);
        } catch (ArithmeticException e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testLength() {
        Vector4f v = new Vector4f(3.0f,4.0f,5.0f, 6.0f);
        float length = v.length();
        assertEquals(9.273619f, length, EPSILON);
    }

    @Test
    public void testLengthZero() {
        Vector4f v = new Vector4f(0,0, 0,0);
        float length = v.length();
        assertEquals(0.0f, length, EPSILON);
    }

    @Test
    public void testNormalize() {
        Vector4f v = new Vector4f(3.0f,4.0f, 5.0f,6.0f);
        Vector4f normalized = v.normalize();
        assertEquals(0.3234983f, normalized.x, EPSILON);
        assertEquals(0.4313311f, normalized.y, EPSILON);
        assertEquals(0.5391638f, normalized.z, EPSILON);
        assertEquals(0.6469966f, normalized.w, EPSILON);
    }

    @Test
    public void testNormalizeZero() {
        Vector4f v = new Vector4f(0,0,0,0);
        try {
            Vector4f normalized = v.normalize();
            assertEquals(0, normalized.x, EPSILON);
            assertEquals(0, normalized.y, EPSILON);
            assertEquals(0, normalized.z, EPSILON);
            assertEquals(0, normalized.w, EPSILON);
        } catch (Exception e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testDot() {
        Vector4f v1 = new Vector4f(1.0f,2.0f,3.0f,4.0f);
        Vector4f v2 = new Vector4f(5.0f, 6.0f, 6.0f, 7.0f);
        float result = v1.dot(v2);
        assertEquals(63, result, EPSILON);
    }

    @Test
    public void testDotZero() {
        Vector4f v1 = new Vector4f(1.0f,2.0f,3.0f, 4.0f);
        Vector4f v2 = new Vector4f(0,0,0,0);
        float result = v1.dot(v2);
        assertEquals(0, result, EPSILON);
    }

    @Test
    public void testEquals() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v3 = new Vector4f(1.1f, 2.0f, 3.0f, 4.0f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testEqualsEpsilon() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(1.0000000001f, 1.9999999999f, 3.0000000003f, 3.9999999999f);
        Vector4f v3 = new Vector4f(1.001f, 2.001f, 3.001f, 4.001f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testHashCode() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v3 = new Vector4f(1.1f, 2.0f, 3.0f, 4.0f);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v3.hashCode());
    }

    @Test
    public void testToString() {
        Vector4f v = new Vector4f(1.5f, 2.5f, 3.5f, 4.5f);
        String str = v.toString();
        assertTrue(str.contains("1.5"));
        assertTrue(str.contains("2.5"));
        assertTrue(str.contains("3.5"));
        assertTrue(str.contains("4.5"));
        assertTrue(str.contains("x"));
        assertTrue(str.contains("y"));
        assertTrue(str.contains("z"));
        assertTrue(str.contains("w"));
        assertTrue(str.startsWith("["));
        assertTrue(str.endsWith("]"));
    }

    @Test
    public void testCase() {
        Vector4f result = new Vector4f(1, 1,1,1)
                .add(new Vector4f(2, 2,2,2))
                .multiply(2)
                .subtract(new Vector4f(1, 1,1,1));

        assertEquals(5, result.x, EPSILON);
        assertEquals(5, result.y, EPSILON);
        assertEquals(5, result.z, EPSILON);
        assertEquals(5, result.w, EPSILON);
    }
}
