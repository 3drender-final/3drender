package com.cgvsu.render_engine;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class RenderEngineTest {

    private static Model makeSingleTriangleModel() {
        Model m = new Model();
        m.vertices.add(new Vector3f(-1, -1, 0));
        m.vertices.add(new Vector3f(1, -1, 0));
        m.vertices.add(new Vector3f(0, 1, 0));

        Polygon p = new Polygon();
        ArrayList<Integer> idx = new ArrayList<>();
        idx.add(0);
        idx.add(1);
        idx.add(2);
        p.setVertexIndices(idx);
        m.polygons.add(p);

        return m;
    }


    @Test
    void shouldRender_FirstCallReturnsTrue() {
        assertTrue(RenderEngine.shouldRender());
    }

    @Test
    void shouldRender_SecondImmediateCallReturnsFalse() {
        RenderEngine.shouldRender();
        assertFalse(RenderEngine.shouldRender());
    }


}
