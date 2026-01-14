package com.cgvsu.math;

public class Matrix4f {
    private final float[][] matrix;
    private static final float EPSILON = 1e-7f;

    public Matrix4f() {
        matrix = new float[4][4];
        setIdentity();
    }

    public Matrix4f(float[][] data) {
        if (data == null || data.length != 4) {
            throw new IllegalArgumentException("Matrix data must be 4x4");
        }
        this.matrix = new float[4][4];
        for (int i = 0; i < 4; i++) {
            if (data[i] == null || data[i].length != 4) {
                throw new IllegalArgumentException("Matrix data must be 4x4");
            }
            System.arraycopy(data[i], 0, matrix[i], 0, 4);
        }
    }

    public Matrix4f(Matrix4f other) {
        this.matrix = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.matrix[i], 0, matrix[i], 0, 4);
        }
    }

    public void setIdentity() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = (i == j) ? 1.0f : 0.0f;
            }
        }
    }

    public void setZero() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] = 0.0f;
            }
        }
    }

    public float get(int i, int j) {
        return matrix[i][j];
    }

    public void set(int i, int j, float value) {
        if (i < 0 || i >= 4 || j < 0 || j >= 4) {
            throw new IndexOutOfBoundsException("Индексы должны быть в промежутке [0..3]");
        }
        this.matrix[i][j] = value;
    }

    public float[][] getData() {
        float[][] copy = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, 4);
        }
        return copy;
    }

    public Matrix4f add(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.matrix[i][j] = matrix[i][j] + other.matrix[i][j];
            }
        }
        return result;
    }

    public Matrix4f subtract(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.matrix[i][j] = matrix[i][j] - other.matrix[i][j];
            }
        }
        return result;
    }

    public Vector4f multiplyVec(Vector4f vec) {
        return new Vector4f(
                matrix[0][0] * vec.x + matrix[0][1] * vec.y + matrix[0][2] * vec.z + matrix[0][3] * vec.w,
                matrix[1][0] * vec.x + matrix[1][1] * vec.y + matrix[1][2] * vec.z + matrix[1][3] * vec.w,
                matrix[2][0] * vec.x + matrix[2][1] * vec.y + matrix[2][2] * vec.z + matrix[2][3] * vec.w,
                matrix[3][0] * vec.x + matrix[3][1] * vec.y + matrix[3][2] * vec.z + matrix[3][3] * vec.w
        );
    }

    public Vector3f multiplyVec(Vector3f vec) {
        Vector4f v4 = new Vector4f(vec.x, vec.y, vec.z, 1f);
        Vector4f result = multiplyVec(v4);
        
        if (result.w == 0f) {
            throw new ArithmeticException("w - некорректная точка");
        }
        
        if (result.w != 1f) {
            return new Vector3f(result.x / result.w, result.y / result.w, result.z / result.w);
        }
        
        return new Vector3f(result.x, result.y, result.z);
    }

    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        result.setZero();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0.0f;
                for (int k = 0; k < 4; k++) {
                    sum += this.matrix[i][k] * other.matrix[k][j];
                }
                result.matrix[i][j] = sum;
            }
        }
        return result;
    }

    public Matrix4f transpose() {
        Matrix4f result = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.matrix[i][j] = matrix[j][i];
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Matrix4f other = (Matrix4f) obj;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (Math.abs(matrix[i][j] - other.matrix[i][j]) > EPSILON) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result = 31 * result + Float.floatToIntBits(matrix[i][j]);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append("[");
            for (int j = 0; j < 4; j++) {
                sb.append(String.format("%.2f", matrix[i][j]));
                if (j < 3) sb.append(" ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}
