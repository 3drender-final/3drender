package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.transformations.AffineTransformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.transformations.AffineTransformation.*;

public class MatrixUtilsTest {

    private static final float EPS = 1e-5f;

    @Test
    void convertIdentityMatrix() {
        Matrix4f ourMatrix = identity();
        javax.vecmath.Matrix4f vecmathMatrix = MatrixUtils.convertToVecmath(ourMatrix);

        assertNotNull(vecmathMatrix);
        assertEquals(1f, vecmathMatrix.m00, EPS);
        assertEquals(1f, vecmathMatrix.m11, EPS);
        assertEquals(1f, vecmathMatrix.m22, EPS);
        assertEquals(1f, vecmathMatrix.m33, EPS);
        assertEquals(0f, vecmathMatrix.m01, EPS);
        assertEquals(0f, vecmathMatrix.m10, EPS);
    }

    @Test
    void convertScaleMatrix() {
        Matrix4f ourMatrix = scale(2f, 3f, 4f);
        javax.vecmath.Matrix4f vecmathMatrix = MatrixUtils.convertToVecmath(ourMatrix);

        assertNotNull(vecmathMatrix);
        assertEquals(2f, vecmathMatrix.m00, EPS);
        assertEquals(3f, vecmathMatrix.m11, EPS);
        assertEquals(4f, vecmathMatrix.m22, EPS);
        assertEquals(1f, vecmathMatrix.m33, EPS);
    }

    @Test
    void convertTranslateMatrix() {
        Matrix4f ourMatrix = translate(5f, -2f, 10f);
        javax.vecmath.Matrix4f vecmathMatrix = MatrixUtils.convertToVecmath(ourMatrix);

        assertNotNull(vecmathMatrix);
        assertEquals(1f, vecmathMatrix.m00, EPS);
        assertEquals(1f, vecmathMatrix.m11, EPS);
        assertEquals(1f, vecmathMatrix.m22, EPS);
        assertEquals(5f, vecmathMatrix.m03, EPS);
        assertEquals(-2f, vecmathMatrix.m13, EPS);
        assertEquals(10f, vecmathMatrix.m23, EPS);
    }

    @Test
    void convertNullReturnsNull() {
        javax.vecmath.Matrix4f result = MatrixUtils.convertToVecmath(null);
        assertNull(result);
    }

    @Test
    void convertedMatrixPreservesTransformation() {
        Matrix4f scale = scale(2f, 2f, 2f);
        javax.vecmath.Matrix4f vecmathScale = MatrixUtils.convertToVecmath(scale);

        assertNotNull(vecmathScale);
        javax.vecmath.Vector3f testVec = new javax.vecmath.Vector3f(1f, 1f, 1f);
        javax.vecmath.Vector3f result = new javax.vecmath.Vector3f();
        vecmathScale.transform(testVec, result);
        
        assertEquals(2f, result.x, EPS);
        assertEquals(2f, result.y, EPS);
        assertEquals(2f, result.z, EPS);
    }
}
