package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    private static final float EPS = 1e-5f;

    @Test
    void cameraConstructor() {
        Vector3f position = new Vector3f(0, 0, 10);
        Vector3f target = new Vector3f(0, 0, 0);
        Camera camera = new Camera(position, target, 1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f camPos = camera.getPosition();
        Vector3f camTarget = camera.getTarget();

        assertEquals(0, camPos.x, EPS);
        assertEquals(0, camPos.y, EPS);
        assertEquals(10, camPos.z, EPS);
        assertEquals(0, camTarget.x, EPS);
        assertEquals(0, camTarget.y, EPS);
        assertEquals(0, camTarget.z, EPS);
    }

    @Test
    void setPosition() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f newPosition = new Vector3f(5, 10, 15);
        camera.setPosition(newPosition);

        Vector3f position = camera.getPosition();
        assertEquals(5, position.x, EPS);
        assertEquals(10, position.y, EPS);
        assertEquals(15, position.z, EPS);
    }

    @Test
    void setTarget() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f newTarget = new Vector3f(1, 2, 3);
        camera.setTarget(newTarget);

        Vector3f target = camera.getTarget();
        assertEquals(1, target.x, EPS);
        assertEquals(2, target.y, EPS);
        assertEquals(3, target.z, EPS);
    }

    @Test
    void setAspectRatio() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        camera.setAspectRatio(16.0f / 9.0f);
        Matrix4f projection = camera.getProjectionMatrix();
        assertNotNull(projection);
    }

    @Test
    void movePosition() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f translation = new Vector3f(1, 2, 3);
        camera.movePosition(translation);

        Vector3f position = camera.getPosition();
        assertEquals(1, position.x, EPS);
        assertEquals(2, position.y, EPS);
        assertEquals(13, position.z, EPS);
    }

    @Test
    void moveTarget() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f translation = new Vector3f(1, 1, 1);
        camera.moveTarget(translation);

        Vector3f target = camera.getTarget();
        assertEquals(1, target.x, EPS);
        assertEquals(1, target.y, EPS);
        assertEquals(1, target.z, EPS);
    }

    @Test
    void getViewMatrix() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Matrix4f viewMatrix = camera.getViewMatrix();
        assertNotNull(viewMatrix);
    }

    @Test
    void getProjectionMatrix() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Matrix4f projectionMatrix = camera.getProjectionMatrix();
        assertNotNull(projectionMatrix);
    }

    @Test
    void getPositionReturnsCopy() {
        Camera camera = new Camera(
                new Vector3f(1, 2, 3),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f pos1 = camera.getPosition();
        Vector3f pos2 = camera.getPosition();

        assertNotSame(pos1, pos2);
        assertEquals(pos1.x, pos2.x, EPS);
        assertEquals(pos1.y, pos2.y, EPS);
        assertEquals(pos1.z, pos2.z, EPS);
    }

    @Test
    void getTargetReturnsCopy() {
        Camera camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(1, 2, 3),
                1.0f, 1.0f, 0.1f, 100.0f);

        Vector3f target1 = camera.getTarget();
        Vector3f target2 = camera.getTarget();

        assertNotSame(target1, target2);
        assertEquals(target1.x, target2.x, EPS);
        assertEquals(target1.y, target2.y, EPS);
        assertEquals(target1.z, target2.z, EPS);
    }

    @Test
    void cameraWithDifferentParameters() {
        Camera camera = new Camera(
                new Vector3f(10, 20, 30),
                new Vector3f(5, 5, 5),
                (float) Math.PI / 4,
                2.0f,
                0.01f,
                1000.0f);

        Vector3f position = camera.getPosition();
        Vector3f target = camera.getTarget();

        assertEquals(10, position.x, EPS);
        assertEquals(20, position.y, EPS);
        assertEquals(30, position.z, EPS);
        assertEquals(5, target.x, EPS);
        assertEquals(5, target.y, EPS);
        assertEquals(5, target.z, EPS);
    }
}
