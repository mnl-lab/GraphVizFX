package com.graphvizfx.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GNodeTest {

    @Test
    void nodeStoresProvidedCoordinatesAndRadius() {
        // Verify constructor keeps id/coordinates and exposes constant radius.
        GNode node = new GNode("n1", 3.5, -2.0);

        assertEquals("n1", node.getId());
        assertEquals(3.5, node.getX());
        assertEquals(-2.0, node.getY());
        assertEquals(20.0, node.getRadius());
    }

    @Test
    void mutatorsUpdateNodeState() {
        // Ensure setters change id and coordinates in place.
        GNode node = new GNode("old", 0.0, 0.0);

        node.setId("new");
        node.setX(12.0);
        node.setY(8.0);

        assertEquals("new", node.getId());
        assertEquals(12.0, node.getX());
        assertEquals(8.0, node.getY());
    }
}
