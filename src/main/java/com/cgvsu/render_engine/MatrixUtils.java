package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;

public class MatrixUtils {
    
    private MatrixUtils() {
    }

    public static javax.vecmath.Matrix4f convertToVecmath(Matrix4f ourMatrix) {
        if (ourMatrix == null) {
            return null;
        }
        
        float[][] data = ourMatrix.getData();
        float[] vecmathData = new float[16];
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                vecmathData[i * 4 + j] = data[i][j];
            }
        }
        
        return new javax.vecmath.Matrix4f(vecmathData);
    }
}
