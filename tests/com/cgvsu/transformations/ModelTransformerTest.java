package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTransformerTest {

    private static final float EPS = 1e-6f;

    @Test
    void DefaultVertexTransformation() {
        ModelTransformer mt = new ModelTransformer();
        AffineTransformation at = new AffineTransformation();

        Vector3f v = new Vector3f(1f, 2f, 3f);
        Matrix4f T = at.translate(5f, -2f, 10f);

        Vector3f actual = mt.transformVertex(v, T);
        Vector3f expected = new Vector3f(6f, 0f, 13f);

        assertEquals(expected.x, actual.x, EPS);
        assertEquals(expected.y, actual.y, EPS);
        assertEquals(expected.z, actual.z, EPS);
    }

    @Test
    void DefaultVertexScale() {
        ModelTransformer mt = new ModelTransformer();
        AffineTransformation at = new AffineTransformation();

        Vector3f v = new Vector3f(1f, 2f, 3f);
        Matrix4f S = at.scale(2f, 3f, 4f);

        Vector3f actual = mt.transformVertex(v, S);
        Vector3f expected = new Vector3f(2f, 6f, 12f);

        assertEquals(expected.x, actual.x, EPS);
        assertEquals(expected.y, actual.y, EPS);
        assertEquals(expected.z, actual.z, EPS);
    }

    @Test
    void TransformVertexWithNormalization() {
        ModelTransformer mt = new ModelTransformer();

        Matrix4f m = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 2f}
        });

        Vector3f v = new Vector3f(2f, 4f, 6f);

        Vector3f actual = mt.transformVertex(v, m);
        Vector3f expected = new Vector3f(1f, 2f, 3f);

        assertEquals(expected.x, actual.x, EPS);
        assertEquals(expected.y, actual.y, EPS);
        assertEquals(expected.z, actual.z, EPS);
    }

    @Test
    void TransformVertexWithW0() {
        ModelTransformer mt = new ModelTransformer();

        Matrix4f m = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 0f}
        });

        Vector3f v = new Vector3f(1f, 2f, 3f);

        assertThrows(ArithmeticException.class, () -> mt.transformVertex(v, m));
    }

    @Test
    void TransformsAllVertices() {
        ModelTransformer mt = new ModelTransformer();
        AffineTransformation at = new AffineTransformation();

        Model model = new Model();
        ArrayList<Vector3f> vertices = new ArrayList<>();
        vertices.add(new Vector3f(1f, 2f, 3f));
        vertices.add(new Vector3f(-1f, 0f, 5f));
        model.vertices = vertices;

        Matrix4f T = at.translate(5f, -2f, 10f);

        mt.transformMatrix(model, T);

        Vector3f v0 = model.vertices.get(0);
        Vector3f v1 = model.vertices.get(1);

        assertEquals(6f, v0.x, EPS);
        assertEquals(0f, v0.y, EPS);
        assertEquals(13f, v0.z, EPS);

        assertEquals(4f, v1.x, EPS);
        assertEquals(-2f, v1.y, EPS);
        assertEquals(15f, v1.z, EPS);
    }

    @Test
    void TransformMatrixThrowsNullPointerException() {
        ModelTransformer mt = new ModelTransformer();
        Matrix4f m = new Matrix4f();

        assertThrows(IllegalArgumentException.class, () -> mt.transformMatrix(null, m));
    }

    @Test
    void TransformMatrixWithEmptyVertices() {
        ModelTransformer mt = new ModelTransformer();
        AffineTransformation at = new AffineTransformation();

        Model model = new Model();
        model.vertices = new ArrayList<>();

        assertDoesNotThrow(() -> mt.transformMatrix(model, at.translate(1f, 2f, 3f)));
        assertTrue(model.vertices.isEmpty());
    }
}
