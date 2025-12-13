package com.graphvizfx.io;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSONExporter {
    
    public static void export(GraphModel graph, File file) throws IOException {
        StringBuilder sb = new StringBuilder("{\n");
        sb.append("  \"directed\": ").append(graph.isDirected()).append(",\n");
        sb.append("  \"weighted\": ").append(graph.isWeighted()).append(",\n");
        sb.append("  \"nodes\": [\n");
        for (int i = 0; i < graph.getNodes().size(); i++) {
            GNode n = graph.getNodes().get(i);
            sb.append(String.format("    {\"id\":\"%s\", \"x\":%.1f, \"y\":%.1f}", n.getId(), n.getX(), n.getY()));
            if (i < graph.getNodes().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n  \"edges\": [\n");
        for (int i = 0; i < graph.getEdges().size(); i++) {
            GEdge e = graph.getEdges().get(i);
            sb.append(String.format("    {\"from\":\"%s\", \"to\":\"%s\", \"weight\":%d}", 
                    e.getSource().getId(), e.getTarget().getId(), e.getWeight()));
            if (i < graph.getEdges().size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n}");
        
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(sb.toString());
        }
    }
}

