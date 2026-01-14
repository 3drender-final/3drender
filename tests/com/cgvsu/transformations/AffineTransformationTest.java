package com.cgvsu.transformations;

import com.cgvsu.math.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AffineTransformationTest {

    @Test
    void ScaleCreatesCorrectMatrix() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.scale(2f, 3f, 4f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {2f, 0f, 0f, 0f},
                {0f, 3f, 0f, 0f},
                {0f, 0f, 4f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void ScaleIdentity() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.scale(1f, 1f, 1f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void TranslateCreatesCorrectMatrix() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.translate(5f, -2f, 10f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 5f},
                {0f, 1f, 0f, -2f},
                {0f, 0f, 1f, 10f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void TranslateIdentity() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.translate(0f, 0f, 0f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void RotateX90() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(90));
        Matrix4f actual = at.rotateX(angle);

        float[][] data = {
                {1f, 0f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, -1f, 0f, 0f},
                {0f, 0f, 0f, 1f}
        };

        Matrix4f expected = new Matrix4f(data);

        assertEquals(actual, expected);
    }

    @Test
    void RotateXArbitrary() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(40));
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        Matrix4f actual = at.rotateX(angle);

        float[][] data = {
                {1f, 0f, 0f, 0f},
                {0f, cos, sin, 0f},
                {0f, -sin, cos, 0f},
                {0f, 0f, 0f, 1f}
        };

        Matrix4f expected = new Matrix4f(data);

        assertEquals(actual, expected);
    }

    @Test
    void RotateY270() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(270));
        Matrix4f actual = at.rotateY(angle);
        float[][] data = {
                {0, 0, -1, 0},
                {0, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 1}
        };
        Matrix4f expected = new Matrix4f(data);
        assertEquals(actual, expected);
    }

    @Test
    void RotateYZeroIsIdentity() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.rotateY(0f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void RotateYArbitrary() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(89));
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        Matrix4f actual = at.rotateY(angle);

        float[][] data = {
                {cos, 0, sin, 0},
                {0, 1, 0, 0},
                {-sin, 0, cos, 0},
                {0, 0, 0, 1}
        };

        Matrix4f expected = new Matrix4f(data);

        assertEquals(actual, expected);
    }

    @Test
    void RotateZ180() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(180));
        Matrix4f actual = at.rotateZ(angle);

        float[][] data = {
                {-1, 0, 0, 0},
                {0, -1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };

        Matrix4f expected = new Matrix4f(data);

        assertEquals(actual, expected);
    }

    @Test
    void RotateZArbitrary() {
        AffineTransformation at = new AffineTransformation();
        float angle = (float) (Math.toRadians(56));
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        Matrix4f actual = at.rotateZ(angle);

        float[][] data = {
                {cos, sin, 0, 0},
                {-sin, cos, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };

        Matrix4f expected = new Matrix4f(data);

        assertEquals(actual, expected);
    }

    @Test
    void RotateXZeroIsIdentity() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.rotateX(0f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }

    @Test
    void Compose() {
        AffineTransformation at = new AffineTransformation();

        float sx = -3f, sy = 3f, sz = 4f;
        float tx = 5f, ty = -2f, tz = 10f;
        float ax = (float) Math.toRadians(14);
        float ay = (float) Math.toRadians(26);
        float az = (float) Math.toRadians(37);

        Matrix4f actual = at.compose(sx, sy, sz, tx, ty, tz, ax, ay, az);

        Matrix4f S = at.scale(sx, sy, sz);
        Matrix4f Rx = at.rotateX(ax);
        Matrix4f Ry = at.rotateY(ay);
        Matrix4f Rz = at.rotateZ(az);
        Matrix4f T = at.translate(tx, ty, tz);

        Matrix4f R = Rz.multiply(Ry).multiply(Rx);
        Matrix4f expected = T.multiply(R).multiply(S);

        assertEquals(expected, actual);
    }

    @Test
    void ComposeAllIdentityParameters() {
        AffineTransformation at = new AffineTransformation();

        Matrix4f actual = at.compose(
                1f, 1f, 1f,
                0f, 0f, 0f,
                0f, 0f, 0f);

        Matrix4f expected = new Matrix4f(new float[][]{
                {1f, 0f, 0f, 0f},
                {0f, 1f, 0f, 0f},
                {0f, 0f, 1f, 0f},
                {0f, 0f, 0f, 1f}
        });

        assertEquals(expected, actual);
    }
}
