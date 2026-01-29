package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.ModelTransform;

/**
 * Построитель матрицы модели (Model Matrix).
 * Отвечает за создание матрицы преобразования из локальных координат в мировые.
 * 
 * Для векторов-столбцов порядок применения трансформаций: сначала масштаб (S), 
 * потом вращение (R), потом перенос (T). Матрица модели: M = T * R * S
 * 
 * При применении к вектору-столбцу v: M * v = T * R * S * v
 * Это означает, что сначала применяется S, потом R, потом T.
 */
public class ModelMatrixBuilder {

    /**
     * Создает единичную матрицу модели (без преобразований).
     * 
     * @return единичная матрица 4x4
     */
    public static Matrix4f identity() {
        return AffineTransformation.identity();
    }

    /**
     * Создает матрицу модели из отдельных компонентов трансформации.
     * 
     * Для векторов-столбцов порядок: M = T * R * S
     * Это означает, что при применении к вектору сначала масштаб, потом вращение, потом перенос.
     * 
     * @param position позиция в мировом пространстве
     * @param rotation вращение в градусах (x, y, z)
     * @param scale масштаб по осям (x, y, z)
     * @return матрица модели 4x4
     */
    public static Matrix4f build(Vector3f position, Vector3f rotation, Vector3f scale) {
        if (position == null || rotation == null || scale == null) {
            throw new IllegalArgumentException("Position, rotation and scale cannot be null");
        }

        Matrix4f scaleMatrix = AffineTransformation.scale(scale.x, scale.y, scale.z);
        
        // Преобразуем градусы в радианы
        float rx = (float) Math.toRadians(rotation.x);
        float ry = (float) Math.toRadians(rotation.y);
        float rz = (float) Math.toRadians(rotation.z);
        
        Matrix4f rotationMatrix = AffineTransformation.rotateX(rx)
                .multiply(AffineTransformation.rotateY(ry))
                .multiply(AffineTransformation.rotateZ(rz));
        
        Matrix4f translationMatrix = AffineTransformation.translate(position.x, position.y, position.z);

        // Порядок для векторов-столбцов: T * R * S
        return translationMatrix.multiply(rotationMatrix).multiply(scaleMatrix);
    }

    /**
     * Создает матрицу модели из объекта ModelTransform.
     * 
     * @param transform объект трансформации модели
     * @return матрица модели 4x4, или единичная матрица если transform == null
     */
    public static Matrix4f build(ModelTransform transform) {
        if (transform == null) {
            return identity();
        }

        return build(
            transform.getPosition(),
            transform.getRotation(),
            transform.getScale()
        );
    }
}
