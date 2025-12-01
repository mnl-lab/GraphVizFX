package com.graphvizfx.model;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class VisualState {
    private Map<String, Color> nodeColors = new HashMap<>();
    private Map<String, Color> edgeColors = new HashMap<>();
    private Map<String, Double> distances = new HashMap<>();
    private String logLine = "";

    public VisualState copy() {
        VisualState copy = new VisualState();
        copy.nodeColors.putAll(this.nodeColors);
        copy.edgeColors.putAll(this.edgeColors);
        copy.distances.putAll(this.distances);
        copy.logLine = this.logLine;
        return copy;
    }

    public Map<String, Color> getNodeColors() {
        return nodeColors;
    }

    public Map<String, Color> getEdgeColors() {
        return edgeColors;
    }

    public Map<String, Double> getDistances() {
        return distances;
    }

    public String getLogLine() {
        return logLine;
    }

    public void setLogLine(String logLine) {
        this.logLine = logLine;
    }
}

