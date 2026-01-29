package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.render_engine.GraphicConveyor.*;

/**
 * Тесты для проверки работы рендеринга с векторами-столбцами.
 */
public class ColumnVectorRenderTest {
    private static final float EPSILON = 1e-5f;

    @Test
    public void testLookAtMatrixStructure() {
        Vector3f eye = new Vector3f(0, 0, 5);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        
        Matrix4f viewMatrix = lookAt(eye, target, up);
        
        // Для векторов-столбцов матрица lookAt должна иметь структуру:
        // [X.x, X.y, X.z, -X·eye]
        // [Y.x, Y.y, Y.z, -Y·eye]
        // [Z.x, Z.y, Z.z, -Z·eye]
        // [0,   0,   0,   1]
        
        // Проверяем, что последняя строка правильная
        assertEquals(0.0f, viewMatrix.get(3, 0), EPSILON);
        assertEquals(0.0f, viewMatrix.get(3, 1), EPSILON);
        assertEquals(0.0f, viewMatrix.get(3, 2), EPSILON);
        assertEquals(1.0f, viewMatrix.get(3, 3), EPSILON);

        assertNotEquals(0.0f, viewMatrix.get(0, 0), EPSILON);
    }

    @Test
    public void testPerspectiveMatrixStructure() {
        Matrix4f projMatrix = perspective(1.0f, 1.0f, 0.1f, 100.0f);

        assertEquals(1.0f, projMatrix.get(3, 2), EPSILON);

        assertNotEquals(0.0f, projMatrix.get(2, 3), EPSILON);
    }

    @Test
    public void testMatrixMultiplicationOrder() {
        Matrix4f model = new Matrix4f();
        Matrix4f view = lookAt(new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        Matrix4f projection = perspective(1.0f, 1.0f, 0.1f, 100.0f);

        Matrix4f mvp1 = projection.multiply(view).multiply(model);

        Matrix4f mvp2 = model.multiply(view).multiply(projection);

        assertFalse(mvp1.equals(mvp2));
    }

    @Test
    public void testVectorTransformation() {
        Matrix4f transform = new Matrix4f(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {1, 2, 3, 1}
        });
        
        Vector3f v = new Vector3f(0, 0, 0);
        Vector3f result = transform.multiplyVec(v);

        assertEquals(1.0f, result.x, EPSILON);
        assertEquals(2.0f, result.y, EPSILON);
        assertEquals(3.0f, result.z, EPSILON);
    }

    @Test
    public void testLookAtTransformation() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        
        Matrix4f viewMatrix = lookAt(eye, target, up);

        Vector3f point = new Vector3f(0, 0, 0);
        Vector3f transformed = viewMatrix.multiplyVec(point);

        assertNotNull(transformed);
    }
}
