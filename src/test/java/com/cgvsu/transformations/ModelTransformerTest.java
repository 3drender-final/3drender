package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.transformations.AffineTransformation.*;
import static com.cgvsu.transformations.ModelTransformer.*;

public class ModelTransformerTest {

    private static final float EPS = 1e-5f;

    @Test
    void transformMatrixTranslatesAllVertices() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 2f, 3f));
        model.vertices.add(new Vector3f(-1f, 0f, 5f));

        Matrix4f translateMatrix = translate(5f, -2f, 10f);
        transformMatrix(model, translateMatrix);

        assertEquals(6f, model.vertices.get(0).x, EPS);
        assertEquals(0f, model.vertices.get(0).y, EPS);
        assertEquals(13f, model.vertices.get(0).z, EPS);

        assertEquals(4f, model.vertices.get(1).x, EPS);
        assertEquals(-2f, model.vertices.get(1).y, EPS);
        assertEquals(15f, model.vertices.get(1).z, EPS);
    }

    @Test
    void transformMatrixScalesAllVertices() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 2f, 3f));
        model.vertices.add(new Vector3f(2f, 4f, 6f));

        Matrix4f scaleMatrix = scale(2f, 3f, 4f);
        transformMatrix(model, scaleMatrix);

        assertEquals(2f, model.vertices.get(0).x, EPS);
        assertEquals(6f, model.vertices.get(0).y, EPS);
        assertEquals(12f, model.vertices.get(0).z, EPS);

        assertEquals(4f, model.vertices.get(1).x, EPS);
        assertEquals(12f, model.vertices.get(1).y, EPS);
        assertEquals(24f, model.vertices.get(1).z, EPS);
    }

    @Test
    void transformMatrixRotatesAllVertices() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 0f, 0f));
        model.vertices.add(new Vector3f(0f, 1f, 0f));

        Matrix4f rotateMatrix = rotateZ((float) Math.PI / 2);
        transformMatrix(model, rotateMatrix);

        assertEquals(0f, model.vertices.get(0).x, EPS);
        assertEquals(1f, model.vertices.get(0).y, EPS);
        assertEquals(0f, model.vertices.get(0).z, EPS);

        assertEquals(-1f, model.vertices.get(1).x, EPS);
        assertEquals(0f, model.vertices.get(1).y, EPS);
        assertEquals(0f, model.vertices.get(1).z, EPS);
    }

    @Test
    void transformMatrixWithCombinedTransformations() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 1f, 1f));

        Matrix4f scale = scale(2f, 2f, 2f);
        Matrix4f translate = translate(1f, 1f, 1f);
        Matrix4f combined = translate.multiply(scale);

        transformMatrix(model, combined);

        assertEquals(3f, model.vertices.get(0).x, EPS);
        assertEquals(3f, model.vertices.get(0).y, EPS);
        assertEquals(3f, model.vertices.get(0).z, EPS);
    }

    @Test
    void transformMatrixWithEmptyVertices() {
        Model model = new Model();
        model.vertices = new ArrayList<>();

        assertDoesNotThrow(() -> transformMatrix(model, translate(1f, 2f, 3f)));
        assertTrue(model.vertices.isEmpty());
    }

    @Test
    void transformMatrixThrowsOnNullModel() {
        Matrix4f matrix = new Matrix4f();
        assertThrows(IllegalArgumentException.class, () -> transformMatrix(null, matrix));
    }

    @Test
    void transformMatrixThrowsOnNullMatrix() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> transformMatrix(model, null));
    }

    @Test
    void transformMatrixPreservesVertexCount() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 2f, 3f));
        model.vertices.add(new Vector3f(4f, 5f, 6f));
        model.vertices.add(new Vector3f(7f, 8f, 9f));

        int originalSize = model.vertices.size();
        transformMatrix(model, scale(2f, 2f, 2f));

        assertEquals(originalSize, model.vertices.size());
    }
}
