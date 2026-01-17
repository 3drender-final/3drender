package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Point2f;

public class GraphicConveyor {

    public static Matrix4f rotateScaleTranslate() {
        return new Matrix4f();
    }

    public static Matrix4f rotateScaleTranslate(Matrix4f modelMatrix) {
        if (modelMatrix == null) {
            return rotateScaleTranslate();
        }
        return new Matrix4f(modelMatrix);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) {
        return CameraView.lookAt(eye, target);
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        return CameraView.lookAt(eye, target, up);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        return CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        return matrix.multiplyVec(vertex);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
