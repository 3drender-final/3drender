package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;
import com.cgvsu.model.Model;
import com.cgvsu.model.ModelTransform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ArrayList;

// УДАЛЕНО: больше не используем rotateScaleTranslate, используем mesh.getModelMatrix()

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            Matrix4f matrix)
    {
        render(graphicsContext, camera, mesh, width, height, matrix);
    }

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final Color baseColor,
            final List<Camera> helperCameras,
            final RenderingModes renderingModes)
    {

        // Используем матрицу модели из объекта mesh
        Matrix4f modelMatrix = Matrix4f.identity();
        Matrix4f viewMatrix = camera.getViewMatrix(); // система координат камеры
        Matrix4f projectionMatrix = camera.getProjectionMatrix(); // плоскость проецирования

        Matrix4f modelViewMatrix = viewMatrix.multiply(modelMatrix);
        Matrix4f modelViewProjectionMatrix = projectionMatrix.multiply(modelViewMatrix);

        ZBuffer zBuffer = new ZBuffer(width, height);

        Color wireColor = Color.BLACK;

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            ArrayList<Integer> vertexIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getVertexIndices());
            final int nVerticesInPolygon = vertexIndices.size();

            if (nVerticesInPolygon < 3) {
                continue;
            }

            Vector3f v0World = mesh.vertices.get(vertexIndices.get(0));
            Vector3f v1World = mesh.vertices.get(vertexIndices.get(1));
            Vector3f v2World = mesh.vertices.get(vertexIndices.get(2));

            // Прямое умножение матрицы на вектор (векторы-столбцы)
            Vector3f v0View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v0World);
            Vector3f v1View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v1World);
            Vector3f v2View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v2World);

            Vector3f edge1 = v1View.subtract(v0View);
            Vector3f edge2 = v2View.subtract(v0View);
            Vector3f faceNormal = edge1.cross(edge2);

            Vector3f toCamera = v0View.multiply(-1.0f);
            boolean frontFacing = faceNormal.dot(toCamera) > 0.0f;

            ArrayList<Integer> textureIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getTextureVertexIndices());
            ArrayList<Integer> normalIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getNormalIndices());

            boolean hasTextureCoords = !textureIndices.isEmpty() && textureIndices.size() == vertexIndices.size();
            boolean hasNormals = !normalIndices.isEmpty() && normalIndices.size() == vertexIndices.size();

            ArrayList<ScreenVertex> screenVertices = new ArrayList<>(nVerticesInPolygon);
            Vector3f cameraPosition = camera.getPosition();

            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f modelVertex = mesh.vertices.get(vertexIndices.get(vertexInPolygonInd));

                // Прямое умножение матрицы на вектор (векторы-столбцы)
                Vector3f worldPosition = GraphicConveyor.multiplyMatrix4ByVector3(modelMatrix, modelVertex);

                // Преобразование в clip-space (4D), далее делим на w, чтобы получить NDC
                Vector4f clipPos = modelViewProjectionMatrix.multiplyVec(
                        new Vector4f(modelVertex.x, modelVertex.y, modelVertex.z, 1.0f));
                float w = clipPos.w;
                float invW = (Math.abs(w) > 1e-7f) ? (1.0f / w) : 1.0f;

                // Нормализованный диапазон (NDC)
                Vector3f transformed = new Vector3f(
                        clipPos.x * invW,
                        clipPos.y * invW,
                        clipPos.z * invW
                );

                Vector2f textureCoords = null;
                if (hasTextureCoords && textureIndices.get(vertexInPolygonInd) < mesh.vertices.size()) {
                    textureCoords = mesh.textureVertices.get(textureIndices.get(vertexInPolygonInd));
                }

                Vector3f worldNormal = null;
                if (hasNormals && normalIndices.get(vertexInPolygonInd) < mesh.normals.size()) {
                    Vector3f modelNormal = mesh.normals.get(normalIndices.get(vertexInPolygonInd));
                    // Для нормалей используем только rotation и scale (без translation)
                    Matrix4f rotationScaleMatrix = Matrix4f.identity();
                    worldNormal = GraphicConveyor.multiplyMatrix4ByVector3(rotationScaleMatrix, modelNormal).normalize();
                }

                ScreenVertex screenVertex = toScreenVertex(
                        transformed,
                        width,
                        height,
                        invW,
                        textureCoords,
                        worldNormal,
                        worldPosition,
                        null
                );
                screenVertices.add(screenVertex);
            }

            // Отрисовка треугольников (заливка)
            // Wireframe не влияет на заливку - это дополнительная обводка поверх заливки
            for (int i = 1; i < nVerticesInPolygon - 1; ++i) {
                ScreenVertex sv0 = screenVertices.get(0);
                ScreenVertex sv1 = screenVertices.get(i);
                ScreenVertex sv2 = screenVertices.get(i + 1);

                // Используем расширенную версию только если включены текстура ИЛИ освещение
                if (renderingModes.isUseTexture() || renderingModes.isUseLighting()) {
                    TriangleRasterization.fillTriangle(
                            graphicsContext,
                            zBuffer,
                            sv0,
                            sv1,
                            sv2,
                            width,
                            height,
                            renderingModes.isUseTexture() ? texture : null,
                            renderingModes.isUseLighting() ? lighting : null,
                            baseColor,
                            cameraPosition
                    );
                } else {
                    // Простая заливка базовым цветом
                    TriangleRasterization.fillTriangle(
                            graphicsContext,
                            zBuffer,
                            sv0,
                            sv1,
                            sv2,
                            width,
                            height,
                            baseColor
                    );
                }
            }

            // Отрисовка полигональной сетки (если включена соответствующая галочка)
            if (renderingModes.isDrawWireframe()) {
                // Оценка коэффициента для depth bias в зависимости от угла поверхности к камере
                Vector3f normalViewNorm = faceNormal.normalize();
                Vector3f toCameraNorm = toCamera.normalize();
                float cosTheta = Math.abs(normalViewNorm.dot(toCameraNorm));
                // 0 — скользящий угол, 1 — фронтально к камере
                float grazing = 1.0f - cosTheta;

                // Нелинейно усиливаем bias только при реально скользящих углах,
                // чтобы при небольших поворотах он оставался ближе к базовому.
                double angleScale = 1.0 + 8.0 * Math.pow(grazing, 5.0);

                // factorr = 1/(1 + k*d)
                float distance = toCamera.length();
                double depthFactor = 1.0 / (1.0 + 0.15 * distance);

                double depthBiasScale = angleScale * depthFactor;

                for (int i = 0; i < nVerticesInPolygon; ++i) {
                    ScreenVertex a = screenVertices.get(i);
                    ScreenVertex b = screenVertices.get((i + 1) % nVerticesInPolygon);
                    LineRasterizer.drawLine(
                            graphicsContext,
                            zBuffer,
                            a,
                            b,
                            width,
                            height,
                            wireColor,
                            depthBiasScale
                    );
                }
            }
        }

        renderHelperCameras(graphicsContext, helperCameras, camera, projectionMatrix.multiply(viewMatrix), width, height);
    }




    private static ScreenVertex toScreenVertex(
            final Vector3f vertex,
            final int width,
            final int height,
            final float invW,
            final Vector2f textureCoords,
            final Vector3f worldNormal,
            final Vector3f worldPosition,
            final Float lightingIntensity) {

        float ndcX = vertex.x;
        float ndcY = vertex.y;
        float ndcZ = vertex.z;

        // координаты на экране (NDC [-1..1] -> pixels [0..width-1] / [0..height-1])
        float screenX = (ndcX + 1.0f) * 0.5f * (width - 1.0f);
        float screenY = (1.0f - ndcY) * 0.5f * (height - 1.0f);

        return new ScreenVertex(screenX, screenY, ndcZ, invW, textureCoords, worldNormal, worldPosition, lightingIntensity);
    }


    private static void renderHelperCameras(
            final GraphicsContext graphicsContext,
            final List<Camera> helperCameras,
            final Camera activeCamera,
            final Matrix4f viewProjectionMatrix,
            final int width,
            final int height) {
        if (helperCameras == null || helperCameras.isEmpty()) {
            return;
        }

        graphicsContext.setFill(Color.CORNFLOWERBLUE);
        for (Camera helper : helperCameras) {
            if (helper == activeCamera) {
                continue;
            }
            Vector4f clip4 = viewProjectionMatrix.multiplyVec(
                    new Vector4f(helper.getPosition().x, helper.getPosition().y, helper.getPosition().z, 1.0f)
            );
            float w = clip4.w;
            // корректнее отсеивать только точки "за" камерой (w<=0),
            // а не резать по ndcZ (диапазон глубины может отличаться)
            if (w <= 1e-7f) {
                continue;
            }

            Vector3f ndc = GraphicConveyor.multiplyMatrix4ByVector3(viewProjectionMatrix, helper.getPosition());
            float screenX = (ndc.x + 1.0f) * 0.5f * (width - 1.0f);
            float screenY = (1.0f - ndc.y) * 0.5f * (height - 1.0f);
            graphicsContext.fillOval(screenX - 4, screenY - 4, 8, 8);
        }
    }
}
