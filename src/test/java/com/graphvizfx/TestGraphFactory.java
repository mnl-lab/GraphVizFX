package com.graphvizfx;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;

/**
 * Utility helpers to build small graphs for deterministic algorithm tests.
 */
public final class TestGraphFactory {
    private TestGraphFactory() {
    }

    public static GraphModel createGraph(boolean directed, boolean weighted, String... nodeIds) {
        GraphModel graph = new GraphModel();
        graph.setDirected(directed);
        graph.setWeighted(weighted);
        double offset = 10.0;
        for (int i = 0; i < nodeIds.length; i++) {
            graph.getNodes().add(new GNode(nodeIds[i], offset * i, offset * i));
        }
        return graph;
    }

    public static GEdge connect(GraphModel graph, String fromId, String toId, int weight) {
        GNode from = graph.getNode(fromId);
        GNode to = graph.getNode(toId);
        if (from == null || to == null) {
            throw new IllegalArgumentException("Missing node for edge: " + fromId + "->" + toId);
        }
        GEdge edge = new GEdge(from, to, weight);
        graph.getEdges().add(edge);
        return edge;
    }
}
