package com.graphvizfx.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GEdgeTest {

    @Test
    void edgeIdReflectsSourceAndTargetChanges() {
        // Edge identifier should track source/target updates for uniqueness.
        GNode a = new GNode("A", 0, 0);
        GNode b = new GNode("B", 0, 0);
        GNode c = new GNode("C", 0, 0);
        GEdge edge = new GEdge(a, b, 5);

        assertEquals("A->B", edge.getId());

        edge.setSource(c);
        assertEquals("C->B", edge.getId());

        edge.setTarget(a);
        assertEquals("C->A", edge.getId());
    }

    @Test
    void edgeWeightMutatorUpdatesStoredValue() {
        // Weight changes should be reflected without altering endpoints.
        GNode a = new GNode("A", 0, 0);
        GNode b = new GNode("B", 0, 0);
        GEdge edge = new GEdge(a, b, 1);

        edge.setWeight(9);

        assertSame(a, edge.getSource());
        assertSame(b, edge.getTarget());
        assertEquals(9, edge.getWeight());
    }
}
