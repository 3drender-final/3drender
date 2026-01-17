package com.cgvsu.render_engine;

import com.cgvsu.math.Matrix4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CameraProjectionTest {

    private static final float EPS = 1e-5f;

    @Test
    void perspectiveWithValidParameters() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 16.0f / 9.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithFovInDegrees() {
        float fov = 90.0f;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithFovInRadians() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveThrowsOnInvalidAspectRatio() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 0.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        assertThrows(IllegalArgumentException.class, () -> {
            CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);
        });
    }

    @Test
    void perspectiveThrowsOnEqualNearAndFarPlane() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 1.0f;
        float nearPlane = 10.0f;
        float farPlane = 10.0f;

        assertThrows(IllegalArgumentException.class, () -> {
            CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);
        });
    }

    @Test
    void perspectiveWithWideAspectRatio() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 21.0f / 9.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithNarrowAspectRatio() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 4.0f / 3.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithSmallFov() {
        float fov = 0.1f;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithLargeFov() {
        float fov = (float) Math.PI / 2;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithLargeFarPlane() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 10000.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveWithSmallNearPlane() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 1.0f;
        float nearPlane = 0.001f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
    }

    @Test
    void perspectiveMatrixStructure() {
        float fov = (float) Math.PI / 4;
        float aspectRatio = 1.0f;
        float nearPlane = 0.1f;
        float farPlane = 100.0f;

        Matrix4f projection = CameraProjection.perspective(fov, aspectRatio, nearPlane, farPlane);

        assertNotNull(projection);
        assertEquals(1.0f, projection.get(2, 3), EPS);
    }
}
