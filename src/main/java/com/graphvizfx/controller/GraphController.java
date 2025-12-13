package com.graphvizfx.controller;

import com.graphvizfx.algorithms.AlgorithmEngine;
import com.graphvizfx.io.JSONExporter;
import com.graphvizfx.io.JSONImporter;
import com.graphvizfx.io.OSMImporter;
import com.graphvizfx.layout.CircularLayout;
import com.graphvizfx.layout.ForceDirectedLayout;
import com.graphvizfx.layout.GridLayout;
import com.graphvizfx.layout.LayoutAlgorithm;
import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Coordinates user actions between the view and the {@link GraphModel}.
 */
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
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be null or empty");
        }
        // Check for duplicate IDs
        if (graph.getNode(id) != null) {
            throw new IllegalArgumentException("Node with ID '" + id + "' already exists");
        }
        graph.getNodes().add(new GNode(id, x, y));
    }

    public void deleteNode(GNode node) {
        graph.getNodes().remove(node);
        graph.getEdges().removeIf(edge -> edge.getSource() == node || edge.getTarget() == node);
    }

    public void addEdge(GNode source, GNode target, int weight) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Edge source and target cannot be null");
        }
        if (source == target) {
            throw new IllegalArgumentException("Cannot create self-loop edge");
        }
        if (graph.hasEdge(source, target)) {
            throw new IllegalArgumentException("Edge already exists between " + source.getId() + " and " + target.getId());
        }
        graph.getEdges().add(new GEdge(source, target, weight));
    }

    public void deleteEdge(GEdge edge) {
        graph.getEdges().remove(edge);
    }

    public int getNextNodeId() {
        return nodeCounter++;
    }

    /**
     * Executes the requested algorithm and returns the full visual trace.
     *
     * @param algo  algorithm identifier
     * @param start optional start node (ignored when not required)
     * @param goal  optional destination node (used by goal-driven algorithms)
     * @return list of visual snapshots representing each step
     */
    public List<VisualState> executeAlgorithm(String algo, GNode start, GNode goal) {
        return AlgorithmEngine.execute(algo, graph, start, goal);
    }

    public void applyLayout(String layoutType, double width, double height) {
        if (layoutType == null) {
            throw new IllegalArgumentException("Layout type cannot be null");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid canvas dimensions: " + width + "x" + height);
        }
        if (graph.getNodes().isEmpty()) {
            return; // Nothing to layout
        }
        
        LayoutAlgorithm layout = null;
        switch (layoutType) {
            case "Circular":
                layout = new CircularLayout();
                break;
            case "Grid":
                layout = new GridLayout();
                break;
            case "Force-Directed":
                layout = new ForceDirectedLayout();
                break;
            default:
                throw new IllegalArgumentException("Unknown layout type: " + layoutType);
        }
        layout.apply(graph, width, height);
    }

    public void exportJSON(File file) throws IOException {
        JSONExporter.export(graph, file);
    }

    public void importJSON(File file) throws IOException {
        JSONImporter.importGraph(graph, file);
    }

    public void importOSM(File file) throws IOException {
        OSMImporter.importGraph(graph, file);
    }
}

