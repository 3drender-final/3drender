package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Vector2fTest {

    private static final float EPSILON = 1e-7f;

    @Test
    public void testConstructors() {
        Vector2f v1 = new Vector2f();
        assertEquals(0.0f, v1.x, EPSILON);
        assertEquals(0.0f, v1.y, EPSILON);

        Vector2f v2 = new Vector2f(1.5f, 2.5f);
        assertEquals(1.5f, v2.x, EPSILON);
        assertEquals(2.5f, v2.y, EPSILON);

        Vector2f v3 = new Vector2f(v2);
        assertEquals(1.5f, v3.x, EPSILON);
        assertEquals(2.5f, v3.y, EPSILON);
    }

    @Test
    public void testAdd() {
        Vector2f v1 = new Vector2f(1,0);
        Vector2f v2 = new Vector2f(0,1);
        Vector2f result = new Vector2f(1,1);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx2() {
        Vector2f v1 = new Vector2f(-1,0);
        Vector2f v2 = new Vector2f(-2,-1);
        Vector2f result = new Vector2f(-3,-1);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx3() {
        Vector2f v1 = new Vector2f(1.2f,2.1f);
        Vector2f v2 = new Vector2f(0.8f,1.9f);
        Vector2f result = v1.add(v2);
        assertEquals(2.0f, result.x, EPSILON);
        assertEquals(4.0f, result.y, EPSILON);
    }

    @Test
    public void testSubtractx1() {
        Vector2f v1 = new Vector2f(2,3);
        Vector2f v2 = new Vector2f(1,2);
        Vector2f result = new Vector2f(1,1);
        assertEquals(result,v1.subtract(v2));
    }

    @Test
    public void testSubtractx2() {
        Vector2f v1 = new Vector2f(2,3);
        Vector2f v2 = new Vector2f(2,7);
        Vector2f result = new Vector2f(0,-4);
        assertEquals(result,v1.subtract(v2));
    }

    @Test
    public void testSubtractx3() {
        Vector2f v1 = new Vector2f(2.0f,3.0f);
        Vector2f v2 = new Vector2f(4.5f,3.0f);
        Vector2f result = v1.subtract(v2);
        assertEquals(-2.5f, result.x, EPSILON);
        assertEquals(0.0f, result.y, EPSILON);
    }

    @Test
    public void testMultiplyx1() {
        Vector2f v = new Vector2f(2,3);
        float scalar = 0.5f;
        Vector2f result = v.multiply(scalar);
        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(1.5f, result.y, EPSILON);
    }

    @Test
    public void testMultiplyx2() {
        Vector2f v = new Vector2f(2,3);
        float scalar = 0;
        Vector2f result = new Vector2f(0,0);
        assertEquals(result, v.multiply(scalar));
    }

    @Test
    public void testMultiplyx3() {
        Vector2f v = new Vector2f(-2.2f,3.3f);
        float scalar = -1.1f;
        Vector2f result = v.multiply(scalar);
        assertEquals(2.42f, result.x, EPSILON);
        assertEquals(-3.63f, result.y, EPSILON);
    }

    @Test
    public void testDivide() {
        Vector2f v = new Vector2f(2.5f,3);
        float scalar = 0.5f;
        Vector2f result = v.divide(scalar);
        assertEquals(5.0f, result.x, EPSILON);
        assertEquals(6.0f, result.y, EPSILON);
    }

    @Test
    public void testDivideByZero() {
        Vector2f v = new Vector2f(2.5f,3);
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
        Vector2f v = new Vector2f(2.5f,3);
        try {
            Vector2f result = v.divide(EPSILON);
            assertTrue(result.x > 0);
            assertTrue(result.y > 0);
        } catch (ArithmeticException e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testLength() {
        Vector2f v = new Vector2f(3.0f,4.0f);
        float length = v.length();
        assertEquals(5.0f, length, EPSILON);
    }

    @Test
    public void testLengthZero() {
        Vector2f v = new Vector2f(0,0);
        float length = v.length();
        assertEquals(0.0f, length, EPSILON);
    }

    @Test
    public void testNormalize() {
        Vector2f v = new Vector2f(3.0f,4.0f);
        Vector2f normalized = v.normalize();
        assertEquals(0.6f, normalized.x, EPSILON);
        assertEquals(0.8f, normalized.y, EPSILON);
    }

    @Test
    public void testNormalizeZero() {
        Vector2f v = new Vector2f(0,0);
        try {
            Vector2f normalized = v.normalize();
            assertEquals(0, normalized.x, EPSILON);
            assertEquals(0, normalized.y, EPSILON);
        } catch (Exception e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testDot() {
        Vector2f v1 = new Vector2f(1.0f,2.0f);
        Vector2f v2 = new Vector2f(3.0f,4.0f);
        float result = v1.dot(v2);
        assertEquals(11, result, EPSILON);
    }

    @Test
    public void testDotZero() {
        Vector2f v1 = new Vector2f(1.0f,2.0f);
        Vector2f v2 = new Vector2f(0,0);
        float result = v1.dot(v2);
        assertEquals(0, result, EPSILON);
    }

    @Test
    public void testEquals() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(1.0f, 2.0f);
        Vector2f v3 = new Vector2f(1.1f, 2.0f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testEqualsEpsilon() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(1.0000000001f, 1.9999999999f);
        Vector2f v3 = new Vector2f(1.001f, 2.001f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testHashCode() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(1.0f, 2.0f);
        Vector2f v3 = new Vector2f(1.1f, 2.0f);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v3.hashCode());
    }

    @Test
    public void testToString() {
        Vector2f v = new Vector2f(1.5f, 2.5f);
        String str = v.toString();

        assertTrue(str.contains("1.5"));
        assertTrue(str.contains("2.5"));
        assertTrue(str.contains("x"));
        assertTrue(str.contains("y"));
        assertTrue(str.startsWith("["));
        assertTrue(str.endsWith("]"));
    }

    @Test
    public void testCase() {
        Vector2f result = new Vector2f(1, 1)
                .add(new Vector2f(2, 2))
                .multiply(2)
                .subtract(new Vector2f(1, 1));

        assertEquals(5, result.x, EPSILON);
        assertEquals(5, result.y, EPSILON);
    }
}
