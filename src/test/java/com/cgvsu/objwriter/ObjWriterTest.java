package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.objreader.ObjReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ObjWriterTest {

    private static final float EPS = 1e-5f;

    @Test
    void writeVertices(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1.0f, 2.0f, 3.0f));
        model.vertices.add(new Vector3f(-1.5f, 0.0f, 5.5f));
        model.vertices.add(new Vector3f(0.0f, -2.5f, -3.0f));

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("v 1.000000 2.000000 3.000000"));
        assertTrue(content.contains("v -1.500000 0.000000 5.500000"));
        assertTrue(content.contains("v 0.000000 -2.500000 -3.000000"));
    }

    @Test
    void writeTextureVertices(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.textureVertices.add(new Vector2f(0.0f, 0.0f));
        model.textureVertices.add(new Vector2f(1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.5f, 1.0f));

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("vt 0.000000 0.000000"));
        assertTrue(content.contains("vt 1.000000 0.000000"));
        assertTrue(content.contains("vt 0.500000 1.000000"));
    }

    @Test
    void writeNormals(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
        model.normals.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.normals.add(new Vector3f(0.0f, 1.0f, 0.0f));

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("vn 0.000000 0.000000 1.000000"));
        assertTrue(content.contains("vn 1.000000 0.000000 0.000000"));
        assertTrue(content.contains("vn 0.000000 1.000000 0.000000"));
    }

    @Test
    void writePolygonsWithVerticesOnly(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1 2 3") || content.contains("f 1 2 3\n"));
    }

    @Test
    void writePolygonsWithVerticesAndTexture(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 0.0f));
        model.textureVertices.add(new Vector2f(1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1/1 2/2 3/3") || content.contains("f 1/1 2/2 3/3\n"));
    }

    @Test
    void writePolygonsWithVerticesAndNormals(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setNormalIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1//1 2//2 3//3") || content.contains("f 1//1 2//2 3//3\n"));
    }

    @Test
    void writePolygonsWithAllAttributes(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 0.0f));
        model.textureVertices.add(new Vector2f(1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 1.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setNormalIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1/1/1 2/2/2 3/3/3") || content.contains("f 1/1/1 2/2/2 3/3/3\n"));
    }

    @Test
    void writeThrowsOnNullModel(@TempDir Path tempDir) {
        Path file = tempDir.resolve("test.obj");
        assertThrows(IllegalArgumentException.class, () -> ObjWriter.write(null, file.toString()));
    }

    @Test
    void writeEmptyModel(@TempDir Path tempDir) throws IOException {
        Model model = new Model();

        Path file = tempDir.resolve("test.obj");
        assertDoesNotThrow(() -> ObjWriter.write(model, file.toString()));

        String content = Files.readString(file);
        assertTrue(content.isEmpty() || content.trim().isEmpty());
    }

    @Test
    void writeCompleteModel(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 0.0f));
        model.textureVertices.add(new Vector2f(1.0f, 0.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 0)));
        polygon.setNormalIndices(new ArrayList<>(java.util.Arrays.asList(0, 0, 0)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("v "));
        assertTrue(content.contains("vt "));
        assertTrue(content.contains("vn "));
        assertTrue(content.contains("f "));
    }

    @Test
    void writeAndReadRoundTrip(@TempDir Path tempDir) throws IOException {
        Model originalModel = new Model();
        originalModel.vertices.add(new Vector3f(1.0f, 2.0f, 3.0f));
        originalModel.vertices.add(new Vector3f(4.0f, 5.0f, 6.0f));
        originalModel.textureVertices.add(new Vector2f(0.0f, 0.0f));
        originalModel.textureVertices.add(new Vector2f(1.0f, 1.0f));
        originalModel.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1)));
        polygon.setTextureVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1)));
        polygon.setNormalIndices(new ArrayList<>(java.util.Arrays.asList(0, 0)));
        originalModel.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(originalModel, file.toString());

        String fileContent = Files.readString(file);
        Model readModel = ObjReader.read(fileContent);

        assertEquals(originalModel.vertices.size(), readModel.vertices.size());
        assertEquals(originalModel.textureVertices.size(), readModel.textureVertices.size());
        assertEquals(originalModel.normals.size(), readModel.normals.size());
        assertEquals(originalModel.polygons.size(), readModel.polygons.size());

        for (int i = 0; i < originalModel.vertices.size(); i++) {
            Vector3f original = originalModel.vertices.get(i);
            Vector3f read = readModel.vertices.get(i);
            assertEquals(original.x, read.x, EPS);
            assertEquals(original.y, read.y, EPS);
            assertEquals(original.z, read.z, EPS);
        }
    }

    @Test
    void writeMultiplePolygons(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 1.0f, 0.0f));

        Polygon polygon1 = new Polygon();
        polygon1.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon1);

        Polygon polygon2 = new Polygon();
        polygon2.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(1, 3, 2)));
        model.polygons.add(polygon2);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        long faceCount = content.lines().filter(line -> line.startsWith("f ")).count();
        assertEquals(2, faceCount);
    }

    @Test
    void writePolygonWithFourVertices(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 1.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2, 3)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1 2 3 4") || content.contains("f 1 2 3 4\n"));
    }

    @Test
    void writeIndicesStartFromOne(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1 2") || content.contains("f 1 2\n"));
        assertFalse(content.contains("f 0 1"));
    }

    @Test
    void writePrecisionFormatting(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(1.234567f, -0.000001f, 999.999999f));

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("v 1.234567") || content.contains("v 1.234568"));
        assertTrue(content.contains("-0.000001") || content.contains("0.000000"));
        assertTrue(content.contains("999.999999") || content.contains("1000.000000"));
    }

    @Test
    void writePolygonWithMixedAttributes(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));
        model.textureVertices.add(new Vector2f(0.0f, 0.0f));
        model.textureVertices.add(new Vector2f(1.0f, 0.0f));
        model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        polygon.setTextureVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 0)));
        polygon.setNormalIndices(new ArrayList<>(java.util.Arrays.asList(0, 0, 0)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("f 1/1/1 2/2/1 3/1/1") || content.contains("f 1/1/1 2/2/1 3/1/1\n"));
    }

    @Test
    void writeModelWithOnlyVerticesAndPolygons(@TempDir Path tempDir) throws IOException {
        Model model = new Model();
        model.vertices.add(new Vector3f(0.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(1.0f, 0.0f, 0.0f));
        model.vertices.add(new Vector3f(0.0f, 1.0f, 0.0f));

        Polygon polygon = new Polygon();
        polygon.setVertexIndices(new ArrayList<>(java.util.Arrays.asList(0, 1, 2)));
        model.polygons.add(polygon);

        Path file = tempDir.resolve("test.obj");
        ObjWriter.write(model, file.toString());

        String content = Files.readString(file);
        assertTrue(content.contains("v "));
        assertTrue(content.contains("f "));
        assertFalse(content.contains("vt "));
        assertFalse(content.contains("vn "));
    }
}
