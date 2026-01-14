package com.cgvsu.math;

public class Matrix4f {
    private float[][] data;

    public Matrix4f() {
        this.data = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[i][j] = (i == j) ? 1.0f : 0.0f;
            }
        }
    }

    public Matrix4f(float[][] data) {
        if (data == null || data.length != 4) {
            throw new IllegalArgumentException("Matrix data must be 4x4");
        }
        for (int i = 0; i < 4; i++) {
            if (data[i] == null || data[i].length != 4) {
                throw new IllegalArgumentException("Matrix data must be 4x4");
            }
        }
        this.data = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, 4);
        }
    }

    public Matrix4f(Matrix4f other) {
        this.data = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(other.data[i], 0, this.data[i], 0, 4);
        }
    }

    public float get(int row, int col) {
        return data[row][col];
    }

    public void set(int row, int col, float value) {
        data[row][col] = value;
    }

    public float[][] getData() {
        float[][] copy = new float[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(data[i], 0, copy[i], 0, 4);
        }
        return copy;
    }

    public Matrix4f multiply(Matrix4f other) {
        Matrix4f result = new Matrix4f();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                float sum = 0.0f;
                for (int k = 0; k < 4; k++) {
                    sum += this.data[i][k] * other.data[k][j];
                }
                result.data[i][j] = sum;
            }
        }
        return result;
    }

    public Vector4f multiplyVec(Vector4f vec) {
        float x = data[0][0] * vec.x + data[0][1] * vec.y + data[0][2] * vec.z + data[0][3] * vec.w;
        float y = data[1][0] * vec.x + data[1][1] * vec.y + data[1][2] * vec.z + data[1][3] * vec.w;
        float z = data[2][0] * vec.x + data[2][1] * vec.y + data[2][2] * vec.z + data[2][3] * vec.w;
        float w = data[3][0] * vec.x + data[3][1] * vec.y + data[3][2] * vec.z + data[3][3] * vec.w;
        return new Vector4f(x, y, z, w);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Matrix4f matrix4f = (Matrix4f) obj;
        final float EPS = 1e-6f;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (Math.abs(data[i][j] - matrix4f.data[i][j]) > EPS) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append("[");
            for (int j = 0; j < 4; j++) {
                sb.append(data[i][j]);
                if (j < 3) sb.append(", ");
            }
            sb.append("]");
            if (i < 3) sb.append("\n");
        }
        return sb.toString();
    }
}
