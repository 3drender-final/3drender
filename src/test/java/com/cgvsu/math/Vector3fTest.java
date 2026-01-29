package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Vector3fTest {
    private static final float EPSILON = 1e-7f;

    @Test
    public void testConstructors() {
        Vector3f v1 = new Vector3f();
        assertEquals(0.0f, v1.x, EPSILON);
        assertEquals(0.0f, v1.y, EPSILON);
        assertEquals(0.0f, v1.z, EPSILON);

        Vector3f v2 = new Vector3f(1.5f, 2.5f,3.5f);
        assertEquals(1.5f, v2.x, EPSILON);
        assertEquals(2.5f, v2.y, EPSILON);
        assertEquals(3.5f, v2.z, EPSILON);

        Vector3f v3 = new Vector3f(v2);
        assertEquals(1.5f, v3.x, EPSILON);
        assertEquals(2.5f, v3.y, EPSILON);
        assertEquals(3.5f, v3.z, EPSILON);
    }

    @Test
    public void testAdd() {
        Vector3f v1 = new Vector3f(1,0,1);
        Vector3f v2 = new Vector3f(0,1, 0);
        Vector3f result = new Vector3f(1,1, 1);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx2() {
        Vector3f v1 = new Vector3f(-1,0, 3);
        Vector3f v2 = new Vector3f(-2,-1, -10);
        Vector3f result = new Vector3f(-3,-1, -7);
        assertEquals(result,v1.add(v2));
    }

    @Test
    public void testAddx3() {
        Vector3f v1 = new Vector3f(1.2f,2.1f, 3.3f);
        Vector3f v2 = new Vector3f(0.8f,1.9f, 3.7f);
        Vector3f result = v1.add(v2);
        assertEquals(2.0f, result.x, EPSILON);
        assertEquals(4.0f, result.y, EPSILON);
        assertEquals(7.0f, result.z, EPSILON);
    }

    @Test
    public void testSubtractx1() {
        Vector3f v1 = new Vector3f(2,3, 4);
        Vector3f v2 = new Vector3f(0, 1,2);
        Vector3f result = new Vector3f(2,2, 2);
        assertEquals(result, v1.subtract(v2));
    }

    @Test
    public void testSubtractx2() {
        Vector3f v1 = new Vector3f(2,3, 10);
        Vector3f v2 = new Vector3f(2,7, 1);
        Vector3f result = new Vector3f(0,-4, 9);
        assertEquals(result,v1.subtract(v2));
    }

    @Test
    public void testSubtractx3() {
        Vector3f v1 = new Vector3f(2.0f,3.0f, 4.0f);
        Vector3f v2 = new Vector3f(4.5f,3.0f, 5.5f);
        Vector3f result = v1.subtract(v2);
        assertEquals(-2.5f, result.x, EPSILON);
        assertEquals(0, result.y, EPSILON);
        assertEquals(-1.5f, result.z, EPSILON);
    }

    @Test
    public void testMultiplyx1() {
        Vector3f v = new Vector3f(2,3, 4);
        float scalar = 0.5f;
        Vector3f result = v.multiply(scalar);
        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(1.5f, result.y, EPSILON);
        assertEquals(2, result.z, EPSILON);
    }

    @Test
    public void testMultiplyx2() {
        Vector3f v = new Vector3f(2,3, 4);
        float scalar = 0;
        Vector3f result = new Vector3f(0,0, 0);
        assertEquals(result, v.multiply(scalar));
    }

    @Test
    public void testMultiplyx3() {
        Vector3f v = new Vector3f(-2.2f,3.3f, -4.4f);
        float scalar = -1.1f;
        Vector3f result = v.multiply(scalar);
        assertEquals(2.42f, result.x, EPSILON);
        assertEquals(-3.63f, result.y, EPSILON);
        assertEquals(4.84f, result.z, EPSILON);
    }

    @Test
    public void testDivide() {
        Vector3f v = new Vector3f(2.5f,3, 4.5f);
        float scalar = 0.5f;
        Vector3f result = v.divide(scalar);
        assertEquals(5, result.x, EPSILON);
        assertEquals(6, result.y, EPSILON);
        assertEquals(9, result.z, EPSILON);
    }

    @Test
    public void testDivideByZero() {
        Vector3f v = new Vector3f(2.5f,3, 4.5f);
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
        Vector3f v = new Vector3f(2.5f,3,4.5f);
        try {
            Vector3f result = v.divide(EPSILON);
            assertTrue(result.x > 0);
            assertTrue(result.y > 0);
            assertTrue(result.z > 0);
        } catch (ArithmeticException e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testLength() {
        Vector3f v = new Vector3f(3.0f,4.0f,5.0f);
        float length = v.length();
        assertEquals(7.071068f, length, EPSILON);
    }

    @Test
    public void testLengthZero() {
        Vector3f v = new Vector3f(0,0, 0);
        float length = v.length();
        assertEquals(0.0f, length, EPSILON);
    }

    @Test
    public void testNormalize() {
        Vector3f v = new Vector3f(3.0f,4.0f, 5.0f);
        Vector3f normalized = v.normalize();
        assertEquals(0.42426407f, normalized.x, EPSILON);
        assertEquals(0.56568545f, normalized.y, EPSILON);
        assertEquals(0.70710677f, normalized.z, EPSILON);
    }

    @Test
    public void testNormalizeZero() {
        Vector3f v = new Vector3f(0,0,0);
        try {
            Vector3f normalized = v.normalize();
            assertEquals(0, normalized.x, EPSILON);
            assertEquals(0, normalized.y, EPSILON);
            assertEquals(0, normalized.z, EPSILON);
        } catch (Exception e) {
            fail("There should not be an exception");
        }
    }

    @Test
    public void testDot() {
        Vector3f v1 = new Vector3f(1.0f,2.0f,3.0f);
        Vector3f v2 = new Vector3f(4.0f, 5.0f, 6.0f);
        float result = v1.dot(v2);
        assertEquals(32, result, EPSILON);
    }

    @Test
    public void testDotZero() {
        Vector3f v1 = new Vector3f(1.0f,2.0f,3.0f);
        Vector3f v2 = new Vector3f(0,0,0);
        float result = v1.dot(v2);
        assertEquals(0, result, EPSILON);
    }

    @Test
    public void testCross() {
        Vector3f v1 = new Vector3f(1.0f,2.0f,3.0f);
        Vector3f v2 = new Vector3f(4.0f,5.0f,6.0f);
        Vector3f result = v1.cross(v2);
        assertEquals(-3, result.x, EPSILON);
        assertEquals(6, result.y, EPSILON);
        assertEquals(-3, result.z, EPSILON);
    }

    @Test
    public void testEquals() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f,3.0f);
        Vector3f v2 = new Vector3f(1.0f, 2.0f,3.0f);
        Vector3f v3 = new Vector3f(1.1f, 2.0f,3.1f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testEqualsEpsilon() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(1.0000000001f, 1.9999999999f, 3.0000000003f);
        Vector3f v3 = new Vector3f(1.001f, 2.001f, 3.001f);
        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }

    @Test
    public void testHashCode() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v3 = new Vector3f(1.1f, 2.0f, 3.0f);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v3.hashCode());
    }

    @Test
    public void testToString() {
        Vector3f v = new Vector3f(1.5f, 2.5f, 3.5f);
        String str = v.toString();

        assertTrue(str.contains("1.5"));
        assertTrue(str.contains("2.5"));
        assertTrue(str.contains("3.5"));
        assertTrue(str.contains("x"));
        assertTrue(str.contains("y"));
        assertTrue(str.contains("z"));
        assertTrue(str.startsWith("["));
        assertTrue(str.endsWith("]"));
    }

    @Test
    public void testCase() {
        Vector3f result = new Vector3f(1, 1,1)
                .add(new Vector3f(2, 2,2))
                .multiply(2)
                .subtract(new Vector3f(1, 1,1));

        assertEquals(5, result.x, EPSILON);
        assertEquals(5, result.y, EPSILON);
        assertEquals(5, result.z, EPSILON);
    }
}
