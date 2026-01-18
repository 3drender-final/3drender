package com.cgvsu.render_engine;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScreenVertexTest {

    @Test
    void defaultConstructorSetsInvWTo1AndNoAttributes() {
        ScreenVertex v = new ScreenVertex(1, 2, 3);

        // Проверяем базовые координаты
        assertEquals(1, v.getX());
        assertEquals(2, v.getY());
        assertEquals(3, v.getZ(), 1e-6);

        // По умолчанию invW = 1
        assertEquals(1.0f, v.getInvW(), 1e-6);

        // Атрибуты должны отсутствовать
        assertFalse(v.hasTextureCoords());
        assertFalse(v.hasNormal());
        assertFalse(v.hasWorldPosition());
        assertFalse(v.hasLightingIntensity());

        // Попытка получить отсутствующие атрибуты должна возвращать null
        assertNull(v.getTextureCoords());
        assertNull(v.getNormal());
        assertNull(v.getWorldPosition());
        assertEquals(0.0f, v.getLightingIntensity(), 1e-6);
    }

}