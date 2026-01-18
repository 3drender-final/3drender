package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Vector4f;
import com.cgvsu.model.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.ArrayList;

import static com.cgvsu.model.Model.preprocess;

public class RenderEngine {

    private static final long MIN_RENDER_INTERVAL_MS = 16; // 1000ms / 60 = ~16ms
    private static long lastRenderTime = 0;
    private static final Object renderLock = new Object();

    public static boolean shouldRender() {
        synchronized (renderLock) {
            long currentTime = System.currentTimeMillis();

            if (lastRenderTime == 0) {
                lastRenderTime = currentTime;
                return true;
            }

            long timeSinceLastRender = currentTime - lastRenderTime;

            if (timeSinceLastRender >= MIN_RENDER_INTERVAL_MS) {
                lastRenderTime = currentTime;
                return true;
            }

            return false;
        }
    }

    // Упрощенная версия метода
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height)
    {

        render(graphicsContext, camera, mesh, width, height,
                null, null, Color.LIGHTGRAY, new RenderingModes());
    }

    public static void renderWithMatrix(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Matrix4f matrix)
    {
        render(graphicsContext, camera, mesh, width, height,
                null, null, Color.LIGHTGRAY, new RenderingModes(), matrix);
    }

    // Основной метод рендеринга с полным набором параметров (БЕЗ Matrix4f)
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final Color baseColor,
            final RenderingModes renderingModes)
    {
        Matrix4f modelMatrix = Matrix4f.identity();

        // Вызываем внутренний метод рендеринга
        render(graphicsContext, camera, mesh, width, height,
                texture, lighting, baseColor, renderingModes, modelMatrix);
    }

    // Метод для рендеринга списка моделей (БЕЗ Matrix4f)
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final List<Model> meshes,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final Color baseColor,
            final List<Camera> helperCameras,
            final RenderingModes renderingModes)
    {
        // Проверяем FPS ограничение
        if (shouldRender()) {
            return;
        }

        // Для каждой модели вызываем рендеринг с единичной матрицей
        Matrix4f modelMatrix = Matrix4f.identity();
        for (Model mesh : meshes) {
            if (mesh != null) {
                render(graphicsContext, camera, mesh, width, height,
                        texture, lighting, baseColor, renderingModes, modelMatrix);
            }
        }

    }

    // Фактический рендеринг с Matrix4f
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final Color baseColor,
            final RenderingModes renderingModes,
            final Matrix4f modelMatrix)
    {
        preprocess(mesh);


        // Проверяем renderingModes на null
        final RenderingModes safeRenderingModes =
                (renderingModes != null) ? renderingModes : new RenderingModes();

        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewMatrix = viewMatrix.multiply(modelMatrix);
        Matrix4f modelViewProjectionMatrix = projectionMatrix.multiply(modelViewMatrix);

        ZBuffer zBuffer = new ZBuffer(width, height);

        Color wireColor = Color.BLACK;

        // Проверяем, что модель не null и содержит полигоны
        if (mesh == null || mesh.polygons == null || mesh.polygons.isEmpty()) {
            return;
        }

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            ArrayList<Integer> vertexIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getVertexIndices());
            final int nVerticesInPolygon = vertexIndices.size();

            if (nVerticesInPolygon < 3) {
                continue;
            }

            // Проверяем индексы вершин
            if (vertexIndices.get(0) >= mesh.vertices.size() ||
                    vertexIndices.get(1) >= mesh.vertices.size() ||
                    vertexIndices.get(2) >= mesh.vertices.size()) {
                continue;
            }

            Vector3f v0World = mesh.vertices.get(vertexIndices.get(0));
            Vector3f v1World = mesh.vertices.get(vertexIndices.get(1));
            Vector3f v2World = mesh.vertices.get(vertexIndices.get(2));

            Vector3f v0View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v0World);
            Vector3f v1View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v1World);
            Vector3f v2View = GraphicConveyor.multiplyMatrix4ByVector3(modelViewMatrix, v2World);

            Vector3f edge1 = v1View.subtract(v0View);
            Vector3f edge2 = v2View.subtract(v0View);
            Vector3f faceNormal = edge1.cross(edge2);

            // Проверяем длину нормали (чтобы избежать деления на ноль)
            if (faceNormal.length() < 1e-7f) {
                continue;
            }

            Vector3f toCamera = v0View.multiply(-1.0f);
            boolean frontFacing = faceNormal.dot(toCamera) > 0.0f;

            ArrayList<Integer> textureIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getTextureVertexIndices());
            ArrayList<Integer> normalIndices = new ArrayList<>(mesh.polygons.get(polygonInd).getNormalIndices());

            boolean hasTextureCoords = !textureIndices.isEmpty() && textureIndices.size() == vertexIndices.size();
            boolean hasNormals = !normalIndices.isEmpty() && normalIndices.size() == vertexIndices.size();

            ArrayList<ScreenVertex> screenVertices = new ArrayList<>(nVerticesInPolygon);
            Vector3f cameraPosition = camera.getPosition();

            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                int vertexIndex = vertexIndices.get(vertexInPolygonInd);
                if (vertexIndex >= mesh.vertices.size()) {
                    continue;
                }

                Vector3f modelVertex = mesh.vertices.get(vertexIndex);

                Vector3f worldPosition = GraphicConveyor.multiplyMatrix4ByVector3(modelMatrix, modelVertex);

                Vector4f clipPos = modelViewProjectionMatrix.multiplyVec(
                        new Vector4f(modelVertex.x, modelVertex.y, modelVertex.z, 1.0f));
                float w = clipPos.w;
                float invW = (Math.abs(w) > 1e-7f) ? (1.0f / w) : 1.0f;

                Vector3f transformed = new Vector3f(
                        clipPos.x * invW,
                        clipPos.y * invW,
                        clipPos.z * invW
                );

                Vector2f textureCoords = null;
                if (hasTextureCoords && vertexInPolygonInd < textureIndices.size()) {
                    int texIndex = textureIndices.get(vertexInPolygonInd);
                    if (texIndex >= 0 && texIndex < mesh.textureVertices.size()) {
                        textureCoords = mesh.textureVertices.get(texIndex);
                    }
                }

                Vector3f worldNormal = null;
                if (hasNormals && vertexInPolygonInd < normalIndices.size()) {
                    int normalIndex = normalIndices.get(vertexInPolygonInd);
                    if (normalIndex >= 0 && normalIndex < mesh.normals.size()) {
                        Vector3f modelNormal = mesh.normals.get(normalIndex);
                        worldNormal = GraphicConveyor.multiplyMatrix4ByVector3(modelMatrix, modelNormal).normalize();
                    }
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

            // Если у нас недостаточно вершин для треугольника, пропускаем
            if (screenVertices.size() < 3) {
                continue;
            }

            // Отрисовка треугольников (триангуляция полигонов)
            for (int i = 1; i < screenVertices.size() - 1; ++i) {
                ScreenVertex sv0 = screenVertices.get(0);
                ScreenVertex sv1 = screenVertices.get(i);
                ScreenVertex sv2 = screenVertices.get(i + 1);

                // Проверяем, что вершины валидны
                if (sv0 == null || sv1 == null || sv2 == null) {
                    continue;
                }

                TriangleRasterization.fillTriangle(
                        graphicsContext,
                        zBuffer,
                        sv0,
                        sv1,
                        sv2,
                        width,
                        height,
                        safeRenderingModes.isUseTexture() ? texture : null,
                        safeRenderingModes.isUseLighting() ? lighting : null,
                        baseColor,
                        cameraPosition
                );
            }

            // Отрисовка полигональной сетки
            if (safeRenderingModes.isDrawWireframe()) {
                Vector3f normalViewNorm = faceNormal.normalize();
                Vector3f toCameraNorm = toCamera.normalize();
                float cosTheta = Math.abs(normalViewNorm.dot(toCameraNorm));
                float grazing = 1.0f - cosTheta;

                double angleScale = 1.0 + 8.0 * Math.pow(grazing, 5.0);
                float distance = toCamera.length();
                double depthFactor = 1.0 / (1.0 + 0.15 * distance);
                double depthBiasScale = angleScale * depthFactor;

                for (int i = 0; i < screenVertices.size(); ++i) {
                    ScreenVertex a = screenVertices.get(i);
                    ScreenVertex b = screenVertices.get((i + 1) % screenVertices.size());
                    if (a != null && b != null) {
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
        }

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

        float screenX = (ndcX + 1.0f) * 0.5f * (width - 1.0f);
        float screenY = (1.0f - ndcY) * 0.5f * (height - 1.0f);

        return new ScreenVertex(screenX, screenY, ndcZ, invW, textureCoords,
                worldNormal, worldPosition, lightingIntensity);
    }
}