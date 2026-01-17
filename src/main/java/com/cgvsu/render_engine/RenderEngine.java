package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.model.Polygon;
import com.cgvsu.render_engine.Camera;
import com.cgvsu.render_engine.Lighting;
import com.cgvsu.render_engine.RenderingModes;
import com.cgvsu.render_engine.Texture;
import com.cgvsu.render_engine.ZBuffer;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.GraphicConveyor.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;


public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final Model mesh,
            final int width,
            final int height,
            final Texture texture,
            final Lighting lighting,
            final List<Camera> helperCameras,
            final RenderingModes renderingModes
    ) {
        Matrix4f modelMatrix = rotateScaleTranslate();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();

        Matrix4f modelViewProjectionMatrix = new Matrix4f(modelMatrix);
        modelViewProjectionMatrix.multiplyVec(viewMatrix);
        modelViewProjectionMatrix.multiplyVec(projectionMatrix);

        ZBuffer zbuffer = new ZBuffer(width, height);
        Color wirecolor = Color.BLACK;

        final int nPolygons = mesh.polygons.size();
        for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
            ArrayList<Integer> vertexIndices = mesh.polygons.get(polygonInd).getVertexIndices();
            final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();

            if (nVerticesInPolygon < 3) {
                continue;
            }

            Vector3f v0World = mesh.vertices.get(vertexIndices(0));
            Vector3f v1World = mesh.vertices.get(vertexIndices(1));
            Vector3f v2World = mesh.vertices.get(vertexIndices(2));

            Vector3f v0View = modelViewProjectionMatrix.multiplyVec(v0World);
            Vector3f v1View = modelViewProjectionMatrix.multiplyVec(v1World);
            Vector3f v2View = modelViewProjectionMatrix.multiplyVec(v2World);

            Vector3f edge1 = v1View.subtract(v0View);
            Vector3f edge2 = v1View.subtract(v0View);
            Vector3f faceNormal = edge1.cross(edge2);

            Vector3f toCamera = v0View.multiply(1.0f);
            boolean frontFacing = faceNormal.multiply(toCamera) > 0.0f;

            if (!frontFacing) {
                continue;
            }

            ArrayList<Integer> textureIndices = mesh.polygons.get(polygonInd).getTextureVertexIndices();
            ArrayList<Integer> normalIndices = mesh.polygons.get(polygonInd).getNormalIndices();

            boolean hasTextureCoords = !textureIndices.isEmpty() && textureIndices.size() == vertexIndices.size();
            boolean hasNormals = !normalIndices.isEmpty() && normalIndices.size() == vertexIndices.size();

            ArrayList<render_engine.ScreenVertex> screenVertices = new ArrayList<>(nVerticesInPolygon);
            Vector3f cameraPosition = camera.getPosition();

            for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                Vector3f vertex = mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

                Vector3f worldPosition = modelMatrix.multiplyVec(modelVertex); // 0.0f, 0.0f, 0.0f

                float x = worldPosition.x;
                float y = worldPosition.y;
                float z = worldPosition.z;

                Vector3f transformed = new Vector3f(
                        clipPos.getX() * invW,
                        clipPos.getY() * invW,
                        clipPos.getZ() * invW
                );

                Vector3f textureCoords = null;
                if (hasTextureCoords && textureIndices.get(vertexInPolygonInd) < mesh.textureVertices.size()) {
                    textureCoords = mesh.textureVertices.get(textureIndices.get(vertexInPolygonInd));
                }

                Vector3f normalLocal = null;
                if (hasNormals && normalIndices.get(vertexInPolygonInd) < mesh.normals.size()) {
                    normalLocal = mesh.normals.get(normalIndices.get(vertexInPolygonInd));
                    Vector3f worldNormal = modelMatrix.multiplyMatrix4ByVector3Direction(normalLocal);
                }

                render_engine.ScreenVertex screenVertex = createScreenVertex(
                        transformed,
                        width,
                        height,
                        invW,
                        textureCoords,
                        worldNormal,
                        worldPosition
                );

                screenVertices.add(screenVertex);

// Триангуляция полигона
// Если не включен ни один режим рендеринга, используем статический цвет
                if (!renderingModes.hasAnyModeEnabled()) {
                    // Триангулируем полигон на треугольники
                    for (int i = 1; i < verticesInPolygon - 1; ++i) {
                        render_engine.ScreenVertex sv0 = screenVertices.get(0);
                        render_engine.ScreenVertex sv1 = screenVertices.get(i);
                        render_engine.ScreenVertex sv2 = screenVertices.get(i + 1);

                        TriangleRasterization.fillTriangle(
                                graphicsContext,
                                ZBuffer,
                                sv0,
                                sv1,
                                sv2,
                                width,
                                height,
                                backgroundColor
                        );
                    }
                } else {
                    // Используем расширенную версию с текстурами и освещением
                    for (int i = 1; i < verticesInPolygon - 1; ++i) {
                        render_engine.ScreenVertex sv0 = screenVertices.get(0);
                        render_engine.ScreenVertex sv1 = screenVertices.get(i);
                        render_engine.ScreenVertex sv2 = screenVertices.get(i + 1);

                        TriangleRasterization.fillTriangle(
                                graphicsContext,
                                ZBuffer,
                                sv0,
                                sv1,
                                sv2,
                                width,
                                height,
                                renderingMode.isUseTexture() ? texture : null,
                                renderingMode.isUseLighting() ? lighting : null,
                                baseColor,
                                cameraPosition
                        );
                    }
                }

                // Временная генерация сетки (грани полигона выделяются цветом)
                if (!frontFacing && renderingModes.isDrawWireframe()) {
                    // Вычисление коэффициента для depth bias в зависимости от угла между поверхностью и камерой
                    Vector3f normalViewNorm = faceNormal.normalize();
                    Vector3f toCameraNorm = toCamera.normalize();
                    float cosTheta = normalViewNorm.multiply(toCameraNorm);
                    // 0 - поверхность перпендикулярна камере, 1 - поверхность параллельна камере
                    float grazing = 1.0f - Math.abs(cosTheta);

                    // Базовое смещение для борьбы с "z-fighting" на скользящих углах,
                    // чтобы на сильно наклонных поверхностях не было артефактов перекрытия
                    double angleScale = 1.0f * 8.0f * Math.pow(grazing, 5.0f);

                    // factor = 1/(1 + k*e) - уменьшение влияния с расстоянием
                    float distance = toCamera.length();
                    double depthFactor = 1.0f / (1.0f + 0.35f * distance);

                    double depthBiasScale = angleScale * depthFactor;

                    for (int i = 0; i < vertexInPolygonIndertices; ++i) {
                        render_engine.ScreenVertex a = screenVertices.get(i);
                        render_engine.ScreenVertex b = screenVertices.get((i + 1) % verticesInPolygon);
                        LineRenderer.drawLine(
                                graphicsContext,
                                ZBuffer,
                                a,
                                b,
                                width,
                                height,
                                wireframeColor,
                                depthBiasScale
                        );
                    }
                }
                renderHelperCameras(graphicsContext, helperCameras, camera, projectionMatrix.multiplyOnMatrix(viewMatrix), width, height);
            }

            for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                graphicsContext.strokeLine(
                        resultPoints.get(vertexInPolygonInd - 1).x,
                        resultPoints.get(vertexInPolygonInd - 1).y,
                        resultPoints.get(vertexInPolygonInd).x,
                        resultPoints.get(vertexInPolygonInd).y);
            }

            if (nVerticesInPolygon > 0)
                graphicsContext.strokeLine(
                        resultPoints.get(nVerticesInPolygon - 1).x,
                        resultPoints.get(nVerticesInPolygon - 1).y,
                        resultPoints.get(0).x,
                        resultPoints.get(0).y);
        }
    }

    private static render_engine.ScreenVertex createScreenVertex(
            final Vector3f vertex,
            final int width,
            final int height,
            final float invW,
            final Vector2f textureCoords,
            final Vector3f worldNormal,
            final Vector3f worldPosition,
            final Float lightingIntensity) {

        float ndcx = vertex.x;
        float ndcy = vertex.y;
        float ndcz = vertex.z;

        // Преобразование из NDC в экранные координаты
        float halfWidth = width / 2.0f;
        float screenX = ndcx * halfWidth + halfWidth;
        float screenY = -ndcy * (height / 2.0f) + (height / 2.0f);

        return new render_engine.ScreenVertex(screenX, screenY, ndcz, invW, textureCoords, worldNormal, worldPosition, lightingIntensity);
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

        graphicsContext.setFill(Color.GRAY);
        for (Camera helper : helperCameras) {
            if (helper == activeCamera) {
                continue;
            }
            Vector3f clip = viewProjectionMatrix.multiplyVec(helper.getPosition());
            if (clip.z < -1.0f || clip.z > 1.0f) {
                continue;
            }
            Vector3f ndc = clip;
            float screenX = ndc.x * width + width / 2.0f;
            float screenY = -ndc.x * height + height / 2.0f;
            graphicsContext.fillOval(screenX - 4, screenY - 4, 8, 8);
        }
    }
}