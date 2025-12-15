package com.graphvizfx.layout;

import com.graphvizfx.TestGraphFactory;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LayoutAlgorithmsTest {

    @Test
    void gridLayoutAssignsFinitePositions() {
        // Grid layout should place every node on finite coordinates within the canvas.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B", "C", "D");

        new GridLayout().apply(graph, 400, 400);

        for (GNode node : graph.getNodes()) {
            assertTrue(Double.isFinite(node.getX()));
            assertTrue(Double.isFinite(node.getY()));
            assertTrue(node.getX() > 0);
            assertTrue(node.getY() > 0);
        }
    }

    @Test
    void forceDirectedKeepsNodesWithinBounds() {
        // Force-directed layout should not push nodes outside canvas bounds.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B", "C");
        graph.getNode("A").setX(50);
        graph.getNode("A").setY(60);
        graph.getNode("B").setX(80);
        graph.getNode("B").setY(90);
        graph.getNode("C").setX(110);
        graph.getNode("C").setY(120);
        TestGraphFactory.connect(graph, "A", "B", 1);

        new ForceDirectedLayout().apply(graph, 300, 200);

        for (GNode node : graph.getNodes()) {
            assertTrue(Double.isFinite(node.getX()));
            assertTrue(Double.isFinite(node.getY()));
            double min = node.getRadius() + 5;
            double maxX = 300 - node.getRadius() - 5;
            double maxY = 200 - node.getRadius() - 5;
            assertTrue(node.getX() >= min && node.getX() <= maxX);
            assertTrue(node.getY() >= min && node.getY() <= maxY);
        }
    }

    @Test
    void forceDirectedSkipsWhenCanvasIsInvalid() {
        // Zero or negative dimensions should leave node coordinates untouched.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B");
        double originalAx = graph.getNode("A").getX();
        double originalAy = graph.getNode("A").getY();

        new ForceDirectedLayout().apply(graph, 0, -10);

        assertEquals(originalAx, graph.getNode("A").getX());
        assertEquals(originalAy, graph.getNode("A").getY());
    }
}
