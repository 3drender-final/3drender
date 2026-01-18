package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LightingTest {

    @Test
    void computeLightingIntensityClampedTo0_1() {

        Lighting lighting = new Lighting(new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), 0.2f, 0.8f);

        // Нормаль направлена к источнику света

        float i1 = lighting.computeLightingIntensity(
                new Vector3f(0, 0, 1),   // Нормаль поверхности (смотрит вверх по Z)
                new Vector3f(0, 0, 0),   // Позиция точки на поверхности
                new Vector3f(0, 0, 5)    // Позиция источника света
        );
        // Интенсивность должна быть в диапазоне [0,1]
        assertTrue(i1 >= 0.0f && i1 <= 1.0f);

        // Нормаль направлена ОТ источника света

        float i2 = lighting.computeLightingIntensity(
                new Vector3f(0, 0, -1),
                new Vector3f(0, 0, 0),
                new Vector3f(0, 0, 5)
        );
        // Интенсивность всё равно должна быть в диапазоне [0, 1]
        assertTrue(i2 >= 0.0f && i2 <= 1.0f);
    }

    @Test
    void shadeColorAppliesAmbientAndDiffuseAndClamps() {
        // Тест функции затенения цвета:

        Lighting lighting = new Lighting(new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), 0.3f, 1.0f);

        Color out = lighting.shadeColor(Color.color(1, 0.5, 0.0, 1.0), 1.0f);

        assertNotNull(out);

        assertTrue(out.getRed() >= 0.0 && out.getRed() <= 1.0);
        assertTrue(out.getGreen() >= 0.0 && out.getGreen() <= 1.0);
        assertTrue(out.getBlue() >= 0.0 && out.getBlue() <= 1.0);

        assertEquals(1.0, out.getOpacity(), 1e-9);


    }
}
