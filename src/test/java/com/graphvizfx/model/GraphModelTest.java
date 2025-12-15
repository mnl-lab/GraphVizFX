package com.graphvizfx.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphModelTest {

    @Test
    void getNodeFindsByIdAndReturnsSameInstance() {
        // Ensure retrieval uses id lookup and preserves identity.
        GraphModel graph = new GraphModel();
        GNode a = new GNode("A", 0, 0);
        GNode b = new GNode("B", 0, 0);
        graph.getNodes().add(a);
        graph.getNodes().add(b);

        assertSame(a, graph.getNode("A"));
        assertSame(b, graph.getNode("B"));
        assertNull(graph.getNode("C"));
    }

    @Test
    void hasEdgeUsesNodeIdentityAndDirection() {
        // Edge presence is determined by object identity and stored orientation.
        GraphModel graph = new GraphModel();
        graph.setDirected(true);
        GNode a = new GNode("A", 0, 0);
        GNode b = new GNode("B", 0, 0);
        graph.getNodes().add(a);
        graph.getNodes().add(b);
        GEdge edge = new GEdge(a, b, 1);
        graph.getEdges().add(edge);

        assertTrue(graph.hasEdge(a, b));
        assertFalse(graph.hasEdge(b, a));

        GNode aCopy = new GNode("A", 0, 0);
        assertFalse(graph.hasEdge(aCopy, b));
    }

    @Test
    void clearRemovesAllNodesAndEdges() {
        // Clearing should reset internal storage for reuse.
        GraphModel graph = new GraphModel();
        graph.getNodes().add(new GNode("A", 0, 0));
        graph.getEdges().add(new GEdge(new GNode("A", 0, 0), new GNode("B", 0, 0), 1));

        graph.clear();

        assertTrue(graph.getNodes().isEmpty());
        assertTrue(graph.getEdges().isEmpty());
    }
}
