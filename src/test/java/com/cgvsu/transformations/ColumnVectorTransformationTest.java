package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.transformations.AffineTransformation.*;

/**
 * Тесты для проверки аффинных преобразований с векторами-столбцами.
 */
public class ColumnVectorTransformationTest {
    private static final float EPSILON = 1e-5f;

    @Test
    public void testScale() {
        Matrix4f scale = scale(2.0f, 3.0f, 4.0f);
        Vector3f v = new Vector3f(1, 1, 1);
        
        Vector3f result = scale.multiplyVec(v);
        
        assertEquals(2.0f, result.x, EPSILON);
        assertEquals(3.0f, result.y, EPSILON);
        assertEquals(4.0f, result.z, EPSILON);
    }

    @Test
    public void testTranslate() {
        Matrix4f translate = translate(5.0f, 10.0f, 15.0f);
        Vector3f v = new Vector3f(1, 2, 3);
        
        Vector3f result = translate.multiplyVec(v);

        assertEquals(6.0f, result.x, EPSILON);
        assertEquals(12.0f, result.y, EPSILON);
        assertEquals(18.0f, result.z, EPSILON);
    }

    @Test
    public void testRotateX() {
        Matrix4f rotate = rotateX((float) (Math.PI / 2));
        Vector3f v = new Vector3f(0, 1, 0);
        
        Vector3f result = rotate.multiplyVec(v);

        assertEquals(0.0f, result.x, EPSILON);
        assertEquals(0.0f, result.y, EPSILON);
        assertEquals(1.0f, result.z, EPSILON);
    }

    @Test
    public void testRotateY() {
        Matrix4f rotate = rotateY((float) (Math.PI / 2));
        Vector3f v = new Vector3f(1, 0, 0);
        
        Vector3f result = rotate.multiplyVec(v);

        assertEquals(0.0f, result.x, EPSILON);
        assertEquals(0.0f, result.y, EPSILON);
        assertEquals(-1.0f, result.z, EPSILON);
    }

    @Test
    public void testRotateZ() {
        Matrix4f rotate = rotateZ((float) (Math.PI / 2));
        Vector3f v = new Vector3f(1, 0, 0);
        
        Vector3f result = rotate.multiplyVec(v);

        assertEquals(0.0f, result.x, EPSILON);
        assertEquals(1.0f, result.y, EPSILON);
        assertEquals(0.0f, result.z, EPSILON);
    }

    @Test
    public void testCombinedTransformations() {
        Matrix4f scale = scale(2.0f, 2.0f, 2.0f);
        Matrix4f rotate = rotateZ((float) (Math.PI / 2));
        Matrix4f translate = translate(1.0f, 1.0f, 1.0f);

        Matrix4f combined = translate.multiply(rotate).multiply(scale);
        
        Vector3f v = new Vector3f(1, 0, 0);
        Vector3f result = combined.multiplyVec(v);

        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(3.0f, result.y, EPSILON);
        assertEquals(1.0f, result.z, EPSILON);
    }

    @Test
    public void testTransformationOrder() {
        Matrix4f scale = scale(2.0f, 2.0f, 2.0f);
        Matrix4f translate = translate(1.0f, 1.0f, 1.0f);
        
        Vector3f v = new Vector3f(1, 1, 1);

        Matrix4f order1 = translate.multiply(scale);
        Vector3f result1 = order1.multiplyVec(v);

        Matrix4f order2 = scale.multiply(translate);
        Vector3f result2 = order2.multiplyVec(v);

        assertNotEquals(result1.x, result2.x, EPSILON);
        assertNotEquals(result1.y, result2.y, EPSILON);
        assertNotEquals(result1.z, result2.z, EPSILON);
    }
}
