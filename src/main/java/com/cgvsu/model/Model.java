package com.cgvsu.model;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;

import java.util.*;

public class Model {

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector2f> textureVertices = new ArrayList<Vector2f>();
    public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
    public ArrayList<Polygon> polygons = new ArrayList<Polygon>();


    public List<Vector3f> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public List<Vector2f> getTextureVertices() {
        return Collections.unmodifiableList(textureVertices);
    }

    public List<Vector3f> getNormals() {
        return Collections.unmodifiableList(normals);
    }

    public List<Polygon> getPolygons() {
        return Collections.unmodifiableList(polygons);
    }

    public static void preprocess(Model model) {
        // Подготовка UV: генерация координат при отсутствии данных в модели.

        // Триангуляция: преобразование полигонов (N-угольники -> треугольники).
        triangulate(model);

        // Пересчёт нормалей: нормали граней/вершин для освещения.
        recalculateNormals(model);
    }

    public static void triangulate(Model model) {
        // Источник: исходные полигоны модели.
        List<Polygon> originalPolygons = model.getPolygons();
        List<Polygon> triangulated = new ArrayList<>(originalPolygons.size());

        for (Polygon polygon : originalPolygons) {
            List<Integer> v = polygon.getVertexIndices();
            List<Integer> t = polygon.getTextureVertexIndices();

            int vertexCount = v.size();
            if (vertexCount <= 3) {
                triangulated.add(polygon);
                continue;
            }

            // Разбиение fan-методом: (0, i, i+1).
            for (int i = 1; i < vertexCount - 1; ++i) {
                Polygon triangle = new Polygon();

                ArrayList<Integer> triV = new ArrayList<>(3);
                triV.add(v.get(0));
                triV.add(v.get(i));
                triV.add(v.get(i + 1));
                triangle.setVertexIndices(triV);

                if (!t.isEmpty()) {
                    ArrayList<Integer> triT = new ArrayList<>(3);
                    triT.add(t.get(0));
                    triT.add(t.get(i));
                    triT.add(t.get(i + 1));
                    triangle.setTextureVertexIndices(triT);
                }

                triangle.setNormalIndices(new ArrayList<>());

                triangulated.add(triangle);
            }
        }

        // Перезапись списка полигонов: замена на триангулированный набор.
        model.polygons.clear();
        for (Polygon polygon : triangulated) {
            model.polygons.add(polygon);
        }
    }

    public static void recalculateNormals(Model model) {
        // Проверка входных данных: наличие геометрии.
        int vertexCount = model.vertices.size();
        if (vertexCount == 0 || model.getPolygons().isEmpty()) {
            model.normals.clear();
            return;
        }

        // Накопление нормалей: суммирование нормалей граней по вершинам.
        Vector3f[] normalSums = new Vector3f[vertexCount];
        for (int i = 0; i < vertexCount; ++i) {
            normalSums[i] = new Vector3f(0.0f, 0.0f, 0.0f);
        }

        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vIdx = polygon.getVertexIndices();
            if (vIdx.size() < 3) {
                continue;
            }

            int i0 = vIdx.get(0);
            int i1 = vIdx.get(1);
            int i2 = vIdx.get(2);

            Vector3f p0 = model.vertices.get(i0);
            Vector3f p1 = model.vertices.get(i1);
            Vector3f p2 = model.vertices.get(i2);

            Vector3f edge1 = p1.subtract(p0);
            Vector3f edge2 = p2.subtract(p0);

            Vector3f faceNormal = edge1.cross(edge2);

            // Проверка вырожденных граней: нулевая площадь.
            if (faceNormal.length() < 1e-6f) {
                continue;
            }

            faceNormal = faceNormal.normalize();

            normalSums[i0] = normalSums[i0].add(faceNormal);
            normalSums[i1] = normalSums[i1].add(faceNormal);
            normalSums[i2] = normalSums[i2].add(faceNormal);
        }

        model.normals.clear();

        // Нормализация суммарных нормалей: получение нормали вершины.
        for (int i = 0; i < vertexCount; ++i) {
            Vector3f sum = normalSums[i];
            if (sum.length() < 1e-6f) {

                model.normals.add(new Vector3f(0.0f, 0.0f, 1.0f));
            } else {

                Vector3f n = sum.normalize();
                model.normals.add(new Vector3f(n.x, n.y, n.z));
            }
        }

        // Привязка индексов нормалей: соответствие vertexIndices -> normalIndices.
        for (Polygon polygon : model.getPolygons()) {
            List<Integer> vIdx = polygon.getVertexIndices();
            ArrayList<Integer> nIdx = new ArrayList<>(vIdx.size());
            nIdx.addAll(vIdx);
            polygon.setNormalIndices(nIdx);
        }
    }
}
