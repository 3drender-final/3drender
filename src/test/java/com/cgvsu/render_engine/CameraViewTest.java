package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraViewTest {

    private static final float EPS = 1e-5f;

    @Test
    void lookAtWithDefaultUp() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtWithCustomUp() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target, up);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraAtOrigin() {
        Vector3f eye = new Vector3f(0, 0, 0);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
        Matrix4f identity = Matrix4f.identity();
        assertEquals(identity.get(0, 0), viewMatrix.get(0, 0), EPS);
    }

    @Test
    void lookAtCameraLookingForward() {
        Vector3f eye = new Vector3f(0, 0, 5);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraLookingUp() {
        Vector3f eye = new Vector3f(0, 0, 0);
        Vector3f target = new Vector3f(0, 10, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraLookingDown() {
        Vector3f eye = new Vector3f(0, 10, 0);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraLookingRight() {
        Vector3f eye = new Vector3f(-10, 0, 0);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtWithCustomUpVector() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target, up);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtWithDifferentUpVector() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(1, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target, up);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraVeryCloseToTarget() {
        Vector3f eye = new Vector3f(0, 0, 0.0001f);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtCameraAtTarget() {
        Vector3f eye = new Vector3f(1, 2, 3);
        Vector3f target = new Vector3f(1, 2, 3);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
    }

    @Test
    void lookAtMatrixStructure() {
        Vector3f eye = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Matrix4f viewMatrix = CameraView.lookAt(eye, target);

        assertNotNull(viewMatrix);
        assertEquals(1.0f, viewMatrix.get(3, 3), EPS);
    }
}
