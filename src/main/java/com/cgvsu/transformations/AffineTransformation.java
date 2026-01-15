package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;

/**
 * Утилитарный класс для создания матриц аффинных преобразований.
 * Матрицы можно комбинировать в любом порядке путем умножения.
 */
public class AffineTransformation {
    
    private AffineTransformation() {
    }

    public static Matrix4f scale(float sx, float sy, float sz) {
        float[][] data = {
                {sx, 0, 0, 0},
                {0, sy, 0, 0},
                {0, 0, sz, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(data);
    }

    public static Matrix4f rotateX(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        //Матрица поворота вокруг X для векторов-столбцов (транспонированная)
        float[][] data = {
                {1, 0, 0, 0},
                {0, cos, -sin, 0},
                {0, sin, cos, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(data);
    }

    public static Matrix4f rotateY(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        //матрица поворота вокруг Y для векторов-столбцов
        float[][] data = {
                {cos, 0, -sin, 0},
                {0, 1, 0, 0},
                {sin, 0, cos, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(data);
    }

    public static Matrix4f rotateZ(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        //вокруг Z для векторов-столбцов
        float[][] data = {
                {cos, -sin, 0, 0},
                {sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        return new Matrix4f(data);
    }

    public static Matrix4f translate(float tx, float ty, float tz) {
        //матрица переноса для row-major формата (перенос в последнем столбце)
        float[][] data = {
                {1, 0, 0, tx},
                {0, 1, 0, ty},
                {0, 0, 1, tz},
                {0, 0, 0, 1}
        };
        return new Matrix4f(data);
    }

    public static Matrix4f identity() {
        return new Matrix4f(new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }
}
