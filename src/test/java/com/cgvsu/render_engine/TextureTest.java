package com.cgvsu.render_engine;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TextureTest {

    @Test
    void sampleClampsUvAndReturnsColorFromImage() {
        // Создаём тестовую текстуру 2x2 пикселя
        WritableImage img = new WritableImage(2, 2);
        img.getPixelWriter().setColor(0, 0, Color.RED);
        img.getPixelWriter().setColor(1, 0, Color.GREEN);
        img.getPixelWriter().setColor(0, 1, Color.BLUE);
        img.getPixelWriter().setColor(1, 1, Color.WHITE);

        Texture t = new Texture(img);

        // Проверяем точное сэмплирование
        assertEquals(Color.BLUE, t.sample(0.0f, 0.0f));   // Левый нижний угол
        assertEquals(Color.RED, t.sample(0.0f, 1.0f));    // Левый верхний угол

        // Проверяем координаты вне диапазона [0,1]
        assertNotNull(t.sample(-100.0f, 100.0f));
        assertNotNull(t.sample(100.0f, -100.0f));
        assertNotNull(t.sample(1.5f, 1.5f));
        assertNotNull(t.sample(-0.5f, -0.5f));
    }

    @Test
    void sampleCorrectlyInterpolatesBetweenPixels() {
        // Создаём текстуру 2x1 для проверки интерполяции
        WritableImage img = new WritableImage(2, 1);
        img.getPixelWriter().setColor(0, 0, Color.BLACK);
        img.getPixelWriter().setColor(1, 0, Color.WHITE);

        Texture t = new Texture(img);

        // Проверяем середину между чёрным и белым
        Color middle = t.sample(0.5f, 0.5f);
        assertNotNull(middle);

        // Цвет должен быть серым (примерно R=0.5, G=0.5, B=0.5)
        assertEquals(0.5, middle.getRed(), 0.1);
        assertEquals(0.5, middle.getGreen(), 0.1);
        assertEquals(0.5, middle.getBlue(), 0.1);
    }

    @Test
    void testDifferentTextureSizes() {
        // Проверяем текстуру 1x1
        WritableImage img1x1 = new WritableImage(1, 1);
        img1x1.getPixelWriter().setColor(0, 0, Color.MAGENTA);
        Texture t1 = new Texture(img1x1);
        assertEquals(Color.MAGENTA, t1.sample(0.5f, 0.5f));

        // Проверяем текстуру 4x4
        WritableImage img4x4 = new WritableImage(4, 4);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                img4x4.getPixelWriter().setColor(x, y,
                        Color.color(x / 4.0, y / 4.0, 0.5));
            }
        }
        Texture t2 = new Texture(img4x4);
        assertNotNull(t2.sample(0.25f, 0.75f));
    }
}