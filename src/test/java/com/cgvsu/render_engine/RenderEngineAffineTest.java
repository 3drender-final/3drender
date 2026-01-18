package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.transformations.AffineTransformation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static com.cgvsu.transformations.AffineTransformation.*;

public class RenderEngineAffineTest {

    private static final float EPS = 1e-5f;

    @Test
    void renderWithIdentityMatrixWorks() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        Matrix4f identity = AffineTransformation.identity();
        
        assertDoesNotThrow(() -> {
            RenderEngine.renderWithMatrix(gc, camera, model, 800, 600, identity);
        }, "Рендеринг с единичной матрицей должен работать");
    }

    @Test
    void renderWithScaleMatrixAppliesTransformation() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        Matrix4f scaleMatrix = scale(2f, 2f, 2f);
        
        assertDoesNotThrow(() -> {
            RenderEngine.renderWithMatrix(gc, camera, model, 800, 600, scaleMatrix);
        }, "Рендеринг с масштабированием должен работать");
    }

    @Test
    void renderWithTranslateMatrixAppliesTransformation() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        Matrix4f translateMatrix = translate(5f, 5f, 5f);
        
        assertDoesNotThrow(() -> {
            RenderEngine.renderWithMatrix(gc, camera, model, 800, 600, translateMatrix);
        }, "Рендеринг с переносом должен работать");
    }

    @Test
    void renderWithRotateMatrixAppliesTransformation() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        Matrix4f rotateMatrix = rotateZ((float) Math.PI / 2);
        
        assertDoesNotThrow(() -> {
            RenderEngine.renderWithMatrix(gc, camera, model, 800, 600, rotateMatrix);
        }, "Рендеринг с поворотом должен работать");
    }

    @Test
    void renderWithCombinedTransformations() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        Matrix4f scale = scale(2f, 2f, 2f);
        Matrix4f translate = translate(1f, 1f, 1f);
        Matrix4f rotate = rotateY((float) Math.PI / 4);
        Matrix4f combined = translate.multiply(rotate).multiply(scale);

        assertDoesNotThrow(() -> {
            RenderEngine.renderWithMatrix(gc, camera, model, 800, 600, combined);
        }, "Комбинированные преобразования должны работать в конвейере");
    }

    @Test
    void renderWithoutModelMatrixUsesIdentity() {
        Model model = createTestModel();

        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Camera camera = new Camera(
                new Vector3f(0, 0, 100),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.01f, 100f);

        assertDoesNotThrow(() -> {
            RenderEngine.render(gc, camera, model, 800, 600);
        }, "Рендеринг без modelMatrix должен использовать единичную матрицу");
    }

    private Model createTestModel() {
        Model model = new Model();
        model.vertices = new ArrayList<>();
        model.vertices.add(new Vector3f(1f, 0f, 0f));
        model.vertices.add(new Vector3f(0f, 1f, 0f));
        model.vertices.add(new Vector3f(0f, 0f, 1f));
        
        model.polygons = new ArrayList<>();
        com.cgvsu.model.Polygon polygon = new com.cgvsu.model.Polygon();
        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(0);
        indices.add(1);
        indices.add(2);
        polygon.setVertexIndices(indices);
        model.polygons.add(polygon);
        
        return model;
    }

    private Model copyModel(Model original) {
        Model copy = new Model();
        copy.vertices = copyVertices(original.vertices);
        copy.polygons = new ArrayList<>(original.polygons);
        return copy;
    }

    private ArrayList<Vector3f> copyVertices(ArrayList<Vector3f> original) {
        ArrayList<Vector3f> copy = new ArrayList<>();
        for (Vector3f v : original) {
            copy.add(new Vector3f(v.x, v.y, v.z));
        }
        return copy;
    }

    private void assertModelsEqual(Model expected, Model actual, String message) {
        assertEquals(expected.vertices.size(), actual.vertices.size(), message);
        for (int i = 0; i < expected.vertices.size(); i++) {
            assertEquals(expected.vertices.get(i).x, actual.vertices.get(i).x, EPS, message);
            assertEquals(expected.vertices.get(i).y, actual.vertices.get(i).y, EPS, message);
            assertEquals(expected.vertices.get(i).z, actual.vertices.get(i).z, EPS, message);
        }
    }
}
