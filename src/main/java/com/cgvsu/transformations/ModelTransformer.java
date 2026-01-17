package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;

public class ModelTransformer {
    
    private ModelTransformer() {
    }

    public static Model transformMatrix(Model model, Matrix4f matrix) {
        if (model == null) {
            throw new IllegalArgumentException("Модель не должна быть null");
        }
        if (matrix == null) {
            throw new IllegalArgumentException("Матрица не должна быть null");
        }

        for (int i = 0; i < model.vertices.size(); i++) {
            model.vertices.set(i, matrix.multiplyVec(model.vertices.get(i)));
        }
        return model;
    }
}
