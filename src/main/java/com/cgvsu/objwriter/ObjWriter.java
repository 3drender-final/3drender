package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Утилита для записи модели в формат OBJ.
 */
public class ObjWriter {
    
    private ObjWriter() {
    }

    /**
     * Записывает модель в файл OBJ.
     * 
     * @param model модель для записи
     * @param fileName путь к файлу
     * @throws IOException если произошла ошибка записи
     */
    public static void write(Model model, String fileName) throws IOException {
        if (model == null) {
            throw new IllegalArgumentException("Model не может быть null");
        }
        
        try (FileWriter writer = new FileWriter(fileName)) {
            writeVertices(writer, model.vertices);
            writeTextureVertices(writer, model.textureVertices);
            writeNormals(writer, model.normals);
            writePolygons(writer, model.polygons);
        }
    }

    private static void writeVertices(FileWriter writer, ArrayList<Vector3f> vertices) throws IOException {
        for (Vector3f vertex : vertices) {
            writer.write(String.format("v %.6f %.6f %.6f%n", vertex.x, vertex.y, vertex.z));
        }
    }

    private static void writeTextureVertices(FileWriter writer, ArrayList<Vector2f> textureVertices) throws IOException {
        for (Vector2f textureVertex : textureVertices) {
            writer.write(String.format("vt %.6f %.6f%n", textureVertex.x, textureVertex.y));
        }
    }

    private static void writeNormals(FileWriter writer, ArrayList<Vector3f> normals) throws IOException {
        for (Vector3f normal : normals) {
            writer.write(String.format("vn %.6f %.6f %.6f%n", normal.x, normal.y, normal.z));
        }
    }

    private static void writePolygons(FileWriter writer, ArrayList<Polygon> polygons) throws IOException {
        for (Polygon polygon : polygons) {
            writer.write("f");
            ArrayList<Integer> vertexIndices = polygon.getVertexIndices();
            ArrayList<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            ArrayList<Integer> normalIndices = polygon.getNormalIndices();
            
            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i) + 1;
                
                if (!textureVertexIndices.isEmpty() && !normalIndices.isEmpty()) {
                    int textureIndex = textureVertexIndices.get(i) + 1;
                    int normalIndex = normalIndices.get(i) + 1;
                    writer.write(String.format(" %d/%d/%d", vertexIndex, textureIndex, normalIndex));
                } else if (!textureVertexIndices.isEmpty()) {
                    int textureIndex = textureVertexIndices.get(i) + 1;
                    writer.write(String.format(" %d/%d", vertexIndex, textureIndex));
                } else if (!normalIndices.isEmpty()) {
                    int normalIndex = normalIndices.get(i) + 1;
                    writer.write(String.format(" %d//%d", vertexIndex, normalIndex));
                } else {
                    writer.write(String.format(" %d", vertexIndex));
                }
            }
            writer.write("\n");
        }
    }
}
