package com.graphvizfx.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphModel {
    private List<GNode> nodes = new ArrayList<>();
    private List<GEdge> edges = new ArrayList<>();
    private boolean isDirected = false;
    private boolean isWeighted = false;

    public void clear() {
        nodes.clear();
        edges.clear();
    }

    public GNode getNode(String id) {
        return nodes.stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean hasEdge(GNode u, GNode v) {
        return edges.stream().anyMatch(e -> e.getSource() == u && e.getTarget() == v);
    }

    public List<GNode> getNodes() {
        return nodes;
    }

    public List<GEdge> getEdges() {
        return edges;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public void setDirected(boolean directed) {
        isDirected = directed;
    }

    public boolean isWeighted() {
        return isWeighted;
    }

    public void setWeighted(boolean weighted) {
        isWeighted = weighted;
    }
}
