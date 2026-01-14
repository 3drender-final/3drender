package com.cgvsu.math;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для проверки работы с векторами-столбцами.
 * Векторы-столбцы: v' = M * v (вектор справа от матрицы)
 */

public class ColumnVectorTest {
    private static final float EPSILON = 1e-5f;

    @Test
    public void testMatrixVectorMultiplication() {
        float[][] matrixData = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };
        Matrix4f m = new Matrix4f(matrixData);
        Vector4f v = new Vector4f(1, 2, 3, 4);
        
        Vector4f result = m.multiplyVec(v);
        
        //Ожидаемый результат: M * v
        //[1*1 + 2*2 + 3*3 + 4*4] = [30]
        //[5*1 + 6*2 + 7*3 + 8*4] = [70]
        //[9*1 + 10*2 + 11*3 + 12*4] = [110]
        //[13*1 + 14*2 + 15*3 + 16*4] = [150]
        assertEquals(30.0f, result.x, EPSILON);
        assertEquals(70.0f, result.y, EPSILON);
        assertEquals(110.0f, result.z, EPSILON);
        assertEquals(150.0f, result.w, EPSILON);
    }

    @Test
    public void testIdentityMatrix() {
        Matrix4f identity = new Matrix4f();
        Vector3f v = new Vector3f(1, 2, 3);
        
        Vector3f result = identity.multiplyVec(v);
        
        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(2.0f, result.y, EPSILON);
        assertEquals(3.0f, result.z, EPSILON);
    }

    @Test
    public void testMatrixMultiplicationOrder() {
        //для векторов-столбцов: (M2 * M1) * v = M2 * (M1 * v)
        Matrix4f m1 = new Matrix4f(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {1, 2, 3, 1}
        });
        
        Matrix4f m2 = new Matrix4f(new float[][]{
                {2, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 1}
        });
        
        Vector3f v = new Vector3f(1, 1, 1);

        Matrix4f combined = m2.multiply(m1);
        Vector3f result1 = combined.multiplyVec(v);

        Vector3f intermediate = m1.multiplyVec(v);
        Vector3f result2 = m2.multiplyVec(intermediate);
        
        assertEquals(result1.x, result2.x, EPSILON);
        assertEquals(result1.y, result2.y, EPSILON);
        assertEquals(result1.z, result2.z, EPSILON);
    }
}
