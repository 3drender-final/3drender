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
        return lookAt(eye, target, new Vector3f(0F, 1.0F, 0F));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f resultZ = target.subtract(eye);
        Vector3f resultX = up.cross(resultZ);
        Vector3f resultY = resultZ.cross(resultX);

        resultX = resultX.normalize();
        resultY = resultY.normalize();
        resultZ = resultZ.normalize();

        float[][] matrixData = new float[][]{
                {resultX.x, resultY.x, resultZ.x, 0},
                {resultX.y, resultY.y, resultZ.y, 0},
                {resultX.z, resultY.z, resultZ.z, 0},
                {-resultX.dot(eye), -resultY.dot(eye), -resultZ.dot(eye), 1}
        };
        return new Matrix4f(matrixData);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, 0, tangentMinusOnDegree / aspectRatio);
        result.set(1, 1, tangentMinusOnDegree);
        result.set(2, 2, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(2, 3, 1.0F);
        result.set(3, 2, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        return matrix.multiplyVec(vertex);
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.x * width + width / 2.0F, -vertex.y * height + height / 2.0F);
    }
}
