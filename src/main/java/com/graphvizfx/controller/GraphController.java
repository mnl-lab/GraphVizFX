package com.graphvizfx.controller;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;

public class GraphController {
    private GraphModel graph;
    private int nodeCounter = 1;

    public GraphController() {
        this.graph = new GraphModel();
    }

    public GraphModel getGraph() {
        return graph;
    }

    public void resetGraph() {
        graph.clear();
        nodeCounter = 1;
    }

    public void addNode(String id, double x, double y) {
        graph.getNodes().add(new GNode(id, x, y));
    }

    public void deleteNode(GNode node) {
        graph.getNodes().remove(node);
        graph.getEdges().removeIf(edge -> edge.getSource() == node || edge.getTarget() == node);
    }

    public void addEdge(GNode source, GNode target, int weight) {
        graph.getEdges().add(new GEdge(source, target, weight));
    }

    public void deleteEdge(GEdge edge) {
        graph.getEdges().remove(edge);
    }

    public int getNextNodeId() {
        return nodeCounter++;
    }
}
