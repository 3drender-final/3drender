package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrbitCameraControllerTest {

    private static final float EPS = 1e-5f;
    private Camera camera;
    private OrbitCameraController controller;

    @BeforeEach
    void setUp() {
        camera = new Camera(
                new Vector3f(0, 0, 10),
                new Vector3f(0, 0, 0),
                1.0f, 1.0f, 0.1f, 100.0f);
        controller = new OrbitCameraController(camera);
    }

    @Test
    void constructorWithCamera() {
        OrbitCameraController ctrl = new OrbitCameraController(camera);
        assertNotNull(ctrl);
        assertEquals(camera, ctrl.getCamera());
    }

    @Test
    void constructorWithCameraAndInitialValues() {
        Vector3f initPos = new Vector3f(0, 5, 10);
        Vector3f initTarget = new Vector3f(0, 0, 0);
        OrbitCameraController ctrl = new OrbitCameraController(camera, initPos, initTarget);

        assertNotNull(ctrl);
        Vector3f pos = ctrl.getInitialPosition();
        assertEquals(0, pos.x, EPS);
        assertEquals(5, pos.y, EPS);
        assertEquals(10, pos.z, EPS);
    }

    @Test
    void constructorThrowsOnNullCamera() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrbitCameraController(null);
        });
    }

    @Test
    void constructorThrowsOnNullInitialValues() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrbitCameraController(camera, null, new Vector3f(0, 0, 0));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new OrbitCameraController(camera, new Vector3f(0, 0, 0), null);
        });
    }

    @Test
    void setTranslationSpeed() {
        controller.setTranslationSpeed(1.0f);
        assertEquals(1.0f, controller.getTranslationSpeed(), EPS);
    }

    @Test
    void setRotationSensitivity() {
        controller.setRotationSensitivity(0.02f);
        assertEquals(0.02f, controller.getRotationSensitivity(), EPS);
    }

    @Test
    void setZoomSensitivity() {
        controller.setZoomSensitivity(10.0f);
        assertEquals(10.0f, controller.getZoomSensitivity(), EPS);
    }

    @Test
    void moveForward() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveForward();
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotEquals(initialPos.z, newPos.z, EPS);
    }

    @Test
    void moveBackward() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveBackward();
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotEquals(initialPos.z, newPos.z, EPS);
    }

    @Test
    void moveLeft() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveLeft();
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotEquals(initialPos.x, newPos.x, EPS);
    }

    @Test
    void moveRight() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveRight();
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotEquals(initialPos.x, newPos.x, EPS);
    }

    @Test
    void moveUp() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveUp();
        Vector3f newPos = controller.getCamera().getPosition();

        assertTrue(newPos.y > initialPos.y);
    }

    @Test
    void moveDown() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.moveDown();
        Vector3f newPos = controller.getCamera().getPosition();

        assertTrue(newPos.y < initialPos.y);
    }

    @Test
    void moveInDirection() {
        Vector3f initialPos = controller.getCamera().getPosition();
        Vector3f direction = new Vector3f(1, 0, 0);
        controller.moveInDirection(direction);
        Vector3f newPos = controller.getCamera().getPosition();

        assertTrue(newPos.x > initialPos.x);
    }

    @Test
    void moveInDirectionThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.moveInDirection(null);
        });
    }

    @Test
    void rotateAroundTarget() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.rotateAroundTarget(0.1f, 0.1f);
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotNull(newPos);
    }

    @Test
    void rotateAroundTargetWithPixels() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.rotateAroundTarget(10.0, 10.0);
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotNull(newPos);
    }

    @Test
    void zoom() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.zoom(1.0);
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotNull(newPos);
    }

    @Test
    void zoomIn() {
        Vector3f initialPos = controller.getCamera().getPosition();
        float initialDistance = initialPos.subtract(controller.getCamera().getTarget()).length();
        controller.zoomIn();
        Vector3f newPos = controller.getCamera().getPosition();
        float newDistance = newPos.subtract(controller.getCamera().getTarget()).length();

        assertTrue(newDistance < initialDistance || Math.abs(newDistance - initialDistance) < EPS);
    }

    @Test
    void zoomOut() {
        Vector3f initialPos = controller.getCamera().getPosition();
        float initialDistance = initialPos.subtract(controller.getCamera().getTarget()).length();
        controller.zoomOut();
        Vector3f newPos = controller.getCamera().getPosition();
        float newDistance = newPos.subtract(controller.getCamera().getTarget()).length();

        assertTrue(newDistance > initialDistance || Math.abs(newDistance - initialDistance) < EPS);
    }

    @Test
    void reset() {
        controller.moveForward();
        controller.moveRight();
        controller.rotateAroundTarget(0.5f, 0.5f);

        Vector3f initialPos = controller.getInitialPosition();
        Vector3f initialTarget = controller.getInitialTarget();

        controller.reset();

        Vector3f pos = controller.getCamera().getPosition();
        Vector3f target = controller.getCamera().getTarget();

        assertEquals(initialPos.x, pos.x, EPS);
        assertEquals(initialPos.y, pos.y, EPS);
        assertEquals(initialPos.z, pos.z, EPS);
        assertEquals(initialTarget.x, target.x, EPS);
        assertEquals(initialTarget.y, target.y, EPS);
        assertEquals(initialTarget.z, target.z, EPS);
    }

    @Test
    void setInitialValues() {
        Vector3f newPos = new Vector3f(5, 5, 5);
        Vector3f newTarget = new Vector3f(1, 1, 1);
        controller.setInitialValues(newPos, newTarget);

        Vector3f initPos = controller.getInitialPosition();
        Vector3f initTarget = controller.getInitialTarget();

        assertEquals(5, initPos.x, EPS);
        assertEquals(5, initPos.y, EPS);
        assertEquals(5, initPos.z, EPS);
        assertEquals(1, initTarget.x, EPS);
        assertEquals(1, initTarget.y, EPS);
        assertEquals(1, initTarget.z, EPS);
    }

    @Test
    void setInitialValuesThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.setInitialValues(null, new Vector3f(0, 0, 0));
        });
        assertThrows(IllegalArgumentException.class, () -> {
            controller.setInitialValues(new Vector3f(0, 0, 0), null);
        });
    }

    @Test
    void onMousePressed() {
        controller.onMousePressed(100.0, 200.0);
        assertTrue(controller.isMousePressed());
    }

    @Test
    void onMouseDragged() {
        controller.onMousePressed(100.0, 200.0);
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.onMouseDragged(150.0, 250.0);
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotNull(newPos);
    }

    @Test
    void onMouseDraggedWithoutPress() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.onMouseDragged(150.0, 250.0);
        Vector3f newPos = controller.getCamera().getPosition();

        assertEquals(initialPos.x, newPos.x, EPS);
        assertEquals(initialPos.y, newPos.y, EPS);
        assertEquals(initialPos.z, newPos.z, EPS);
    }

    @Test
    void onMouseReleased() {
        controller.onMousePressed(100.0, 200.0);
        assertTrue(controller.isMousePressed());
        controller.onMouseReleased();
        assertFalse(controller.isMousePressed());
    }

    @Test
    void onMouseScroll() {
        Vector3f initialPos = controller.getCamera().getPosition();
        controller.onMouseScroll(1.0);
        Vector3f newPos = controller.getCamera().getPosition();

        assertNotNull(newPos);
    }

    @Test
    void getInitialPositionReturnsCopy() {
        Vector3f pos1 = controller.getInitialPosition();
        Vector3f pos2 = controller.getInitialPosition();

        assertNotSame(pos1, pos2);
        assertEquals(pos1.x, pos2.x, EPS);
        assertEquals(pos1.y, pos2.y, EPS);
        assertEquals(pos1.z, pos2.z, EPS);
    }

    @Test
    void getInitialTargetReturnsCopy() {
        Vector3f target1 = controller.getInitialTarget();
        Vector3f target2 = controller.getInitialTarget();

        assertNotSame(target1, target2);
        assertEquals(target1.x, target2.x, EPS);
        assertEquals(target1.y, target2.y, EPS);
        assertEquals(target1.z, target2.z, EPS);
    }
}
