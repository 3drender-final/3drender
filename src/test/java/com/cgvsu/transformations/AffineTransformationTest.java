package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.transformations.AffineTransformation.*;

public class AffineTransformationTest {

    private static final float EPS = 1e-5f;

    @Test
    void scaleDoublesVector() {
        Matrix4f scaleMatrix = scale(2f, 2f, 2f);
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f result = scaleMatrix.multiplyVec(v);
        
        assertEquals(2f, result.x, EPS);
        assertEquals(4f, result.y, EPS);
        assertEquals(6f, result.z, EPS);
    }

    @Test
    void scaleWithDifferentAxes() {
        Matrix4f scaleMatrix = scale(2f, 3f, 4f);
        Vector3f v = new Vector3f(1f, 1f, 1f);
        
        Vector3f result = scaleMatrix.multiplyVec(v);
        
        assertEquals(2f, result.x, EPS);
        assertEquals(3f, result.y, EPS);
        assertEquals(4f, result.z, EPS);
    }

    @Test
    void scaleIdentityDoesNotChange() {
        Matrix4f scaleMatrix = scale(1f, 1f, 1f);
        Vector3f v = new Vector3f(5f, 10f, 15f);
        
        Vector3f result = scaleMatrix.multiplyVec(v);
        
        assertEquals(v.x, result.x, EPS);
        assertEquals(v.y, result.y, EPS);
        assertEquals(v.z, result.z, EPS);
    }

    @Test
    void translateMovesVector() {
        Matrix4f translateMatrix = translate(5f, -2f, 10f);
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f result = translateMatrix.multiplyVec(v);
        
        assertEquals(6f, result.x, EPS);
        assertEquals(0f, result.y, EPS);
        assertEquals(13f, result.z, EPS);
    }

    @Test
    void translateZeroDoesNotMove() {
        Matrix4f translateMatrix = translate(0f, 0f, 0f);
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f result = translateMatrix.multiplyVec(v);
        
        assertEquals(v.x, result.x, EPS);
        assertEquals(v.y, result.y, EPS);
        assertEquals(v.z, result.z, EPS);
    }

    @Test
    void rotateX90DegreesRotatesAroundX() {
        Matrix4f rotateMatrix = rotateX((float) Math.PI / 2);
        Vector3f v = new Vector3f(0f, 1f, 0f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(0f, result.x, EPS);
        assertEquals(0f, result.y, EPS);
        assertEquals(1f, result.z, EPS);
    }

    @Test
    void rotateX180DegreesFlipsYZ() {
        Matrix4f rotateMatrix = rotateX((float) Math.PI);
        Vector3f v = new Vector3f(0f, 1f, 0f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(0f, result.x, EPS);
        assertEquals(-1f, result.y, EPS);
        assertEquals(0f, result.z, EPS);
    }

    @Test
    void rotateXDoesNotChangeX() {
        Matrix4f rotateMatrix = rotateX((float) Math.PI / 4);
        Vector3f v = new Vector3f(5f, 0f, 0f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(5f, result.x, EPS);
    }

    @Test
    void rotateY90DegreesRotatesAroundY() {
        Matrix4f rotateMatrix = rotateY((float) Math.PI / 2);
        Vector3f v = new Vector3f(0f, 0f, 1f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(1f, result.x, EPS);
        assertEquals(0f, result.y, EPS);
        assertEquals(0f, result.z, EPS);
    }

    @Test
    void rotateYDoesNotChangeY() {
        Matrix4f rotateMatrix = rotateY((float) Math.PI / 4);
        Vector3f v = new Vector3f(0f, 5f, 0f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(5f, result.y, EPS);
    }

    @Test
    void rotateZ90DegreesRotatesAroundZ() {
        Matrix4f rotateMatrix = rotateZ((float) Math.PI / 2);
        Vector3f v = new Vector3f(1f, 0f, 0f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(0f, result.x, EPS);
        assertEquals(1f, result.y, EPS);
        assertEquals(0f, result.z, EPS);
    }

    @Test
    void rotateZDoesNotChangeZ() {
        Matrix4f rotateMatrix = rotateZ((float) Math.PI / 4);
        Vector3f v = new Vector3f(0f, 0f, 5f);
        
        Vector3f result = rotateMatrix.multiplyVec(v);
        
        assertEquals(5f, result.z, EPS);
    }

    @Test
    void rotateZeroDoesNotChange() {
        Matrix4f rotateX = rotateX(0f);
        Matrix4f rotateY = rotateY(0f);
        Matrix4f rotateZ = rotateZ(0f);
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f resultX = rotateX.multiplyVec(v);
        Vector3f resultY = rotateY.multiplyVec(v);
        Vector3f resultZ = rotateZ.multiplyVec(v);
        
        assertEquals(v.x, resultX.x, EPS);
        assertEquals(v.y, resultX.y, EPS);
        assertEquals(v.z, resultX.z, EPS);
        
        assertEquals(v.x, resultY.x, EPS);
        assertEquals(v.y, resultY.y, EPS);
        assertEquals(v.z, resultY.z, EPS);
        
        assertEquals(v.x, resultZ.x, EPS);
        assertEquals(v.y, resultZ.y, EPS);
        assertEquals(v.z, resultZ.z, EPS);
    }

    @Test
    void identityDoesNotChange() {
        Matrix4f identityMatrix = identity();
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f result = identityMatrix.multiplyVec(v);
        
        assertEquals(v.x, result.x, EPS);
        assertEquals(v.y, result.y, EPS);
        assertEquals(v.z, result.z, EPS);
    }

    @Test
    void combinedScaleAndTranslate() {
        Matrix4f scaleMatrix = scale(2f, 2f, 2f);
        Matrix4f translateMatrix = translate(1f, 1f, 1f);
        Matrix4f combined = translateMatrix.multiply(scaleMatrix);
        
        Vector3f v = new Vector3f(1f, 1f, 1f);
        Vector3f result = combined.multiplyVec(v);
        
        assertEquals(3f, result.x, EPS);
        assertEquals(3f, result.y, EPS);
        assertEquals(3f, result.z, EPS);
    }

    @Test
    void combinedRotateAndTranslate() {
        Matrix4f rotateMatrix = rotateZ((float) Math.PI / 2);
        Matrix4f translateMatrix = translate(1f, 0f, 0f);
        Matrix4f combined = translateMatrix.multiply(rotateMatrix);
        
        Vector3f v = new Vector3f(1f, 0f, 0f);
        Vector3f result = combined.multiplyVec(v);
        
        assertEquals(1f, result.x, EPS);
        assertEquals(1f, result.y, EPS);
        assertEquals(0f, result.z, EPS);
    }

    @Test
    void orderMatters() {
        Matrix4f scale = scale(2f, 2f, 2f);
        Matrix4f translate = translate(1f, 1f, 1f);
        
        Matrix4f scaleThenTranslate = translate.multiply(scale);
        Matrix4f translateThenScale = scale.multiply(translate);
        
        Vector3f v = new Vector3f(1f, 1f, 1f);
        
        Vector3f result1 = scaleThenTranslate.multiplyVec(v);
        Vector3f result2 = translateThenScale.multiplyVec(v);
        
        assertNotEquals(result1.x, result2.x, EPS);
        assertNotEquals(result1.y, result2.y, EPS);
        assertNotEquals(result1.z, result2.z, EPS);
    }

    @Test
    void fullRotation360DegreesReturnsToOriginal() {
        Matrix4f rotateX = rotateX((float) (2 * Math.PI));
        Matrix4f rotateY = rotateY((float) (2 * Math.PI));
        Matrix4f rotateZ = rotateZ((float) (2 * Math.PI));
        
        Vector3f v = new Vector3f(1f, 2f, 3f);
        
        Vector3f resultX = rotateX.multiplyVec(v);
        Vector3f resultY = rotateY.multiplyVec(v);
        Vector3f resultZ = rotateZ.multiplyVec(v);
        
        assertEquals(v.x, resultX.x, EPS);
        assertEquals(v.y, resultX.y, EPS);
        assertEquals(v.z, resultX.z, EPS);
        
        assertEquals(v.x, resultY.x, EPS);
        assertEquals(v.y, resultY.y, EPS);
        assertEquals(v.z, resultY.z, EPS);
        
        assertEquals(v.x, resultZ.x, EPS);
        assertEquals(v.y, resultZ.y, EPS);
        assertEquals(v.z, resultZ.z, EPS);
    }
}
