package com.cgvsu.render_engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RenderingModesTest {

    @Test
    void defaultConstructorHasNoModesEnabled() {
        RenderingModes modes = new RenderingModes();

        // По умолчанию все флаги должны быть false
        assertFalse(modes.isDrawWireframe());
        assertFalse(modes.isUseTexture());
        assertFalse(modes.isUseLighting());
        assertFalse(modes.hasAnyModeEnabled());

        // Проверяем что флаги независимы
        modes.setDrawWireframe(true);
        assertTrue(modes.isDrawWireframe());
        assertFalse(modes.isUseTexture());
        assertFalse(modes.isUseLighting());
    }

    @Test
    void hasAnyModeEnabledTrueWhenAnyFillFlagEnabled() {
        RenderingModes modes = new RenderingModes();

        // Wireframe не считается режимом заливки
        modes.setDrawWireframe(true);
        assertFalse(modes.hasAnyModeEnabled());

        // Текстурирование - это режим заливки
        modes.setDrawWireframe(false);
        modes.setUseTexture(true);
        assertTrue(modes.hasAnyModeEnabled());

        // Освещение - тоже режим заливки
        modes.setUseTexture(false);
        modes.setUseLighting(true);
        assertTrue(modes.hasAnyModeEnabled());

        // Комбинация режимов
        modes.setUseTexture(true);
        modes.setUseLighting(true);
        assertTrue(modes.hasAnyModeEnabled());
    }

    @Test
    void multipleModesCanBeEnabledSimultaneously() {
        RenderingModes modes = new RenderingModes();

        // Можно включить несколько режимов одновременно
        modes.setDrawWireframe(true);
        modes.setUseTexture(true);
        modes.setUseLighting(true);

        assertTrue(modes.isDrawWireframe());
        assertTrue(modes.isUseTexture());
        assertTrue(modes.isUseLighting());
        assertTrue(modes.hasAnyModeEnabled());

        // Проверяем выключение отдельных режимов
        modes.setUseTexture(false);
        assertFalse(modes.isUseTexture());
        assertTrue(modes.isDrawWireframe());
        assertTrue(modes.isUseLighting());
    }
}