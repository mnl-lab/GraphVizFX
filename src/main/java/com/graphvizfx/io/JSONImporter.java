package com.graphvizfx.io;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONImporter {
    
    public static void importGraph(GraphModel graph, File file) throws IOException {
        if (file == null || !file.exists() || !file.canRead()) {
            throw new IOException("File does not exist or cannot be read: " + file);
        }
        
        String content;
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IOException("Failed to read file: " + e.getMessage(), e);
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IOException("File is empty or invalid");
        }
        
        graph.clear();
        
        try {
            Pattern pDir = Pattern.compile("\"directed\"\\s*:\\s*(true|false)");
            Matcher mDir = pDir.matcher(content);
            if (mDir.find()) {
                graph.setDirected(Boolean.parseBoolean(mDir.group(1)));
            }
            
            Pattern pWei = Pattern.compile("\"weighted\"\\s*:\\s*(true|false)");
            Matcher mWei = pWei.matcher(content);
            if (mWei.find()) {
                graph.setWeighted(Boolean.parseBoolean(mWei.group(1)));
            }
            
            Pattern pNode = Pattern.compile("\\{\"id\":\"([^\"]+)\",\\s*\"x\":([0-9.]+),\\s*\"y\":([0-9.]+)\\}");
            Matcher mNode = pNode.matcher(content);
            int nodeCount = 0;
            while (mNode.find()) {
                try {
                    String id = mNode.group(1);
                    double x = Double.parseDouble(mNode.group(2));
                    double y = Double.parseDouble(mNode.group(3));
                    graph.getNodes().add(new GNode(id, x, y));
                    nodeCount++;
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid node coordinates at node " + nodeCount + ": " + e.getMessage());
                }
            }
            
            Pattern pEdge = Pattern.compile("\\{\"from\":\"([^\"]+)\",\\s*\"to\":\"([^\"]+)\",\\s*\"weight\":(-?\\d+)\\}");
            Matcher mEdge = pEdge.matcher(content);
            int edgeCount = 0;
            while (mEdge.find()) {
                try {
                    String fromId = mEdge.group(1);
                    String toId = mEdge.group(2);
                    GNode u = graph.getNode(fromId);
                    GNode v = graph.getNode(toId);
                    if (u == null) {
                        throw new IOException("Edge " + edgeCount + " references unknown node: " + fromId);
                    }
                    if (v == null) {
                        throw new IOException("Edge " + edgeCount + " references unknown node: " + toId);
                    }
                    int weight = Integer.parseInt(mEdge.group(3));
                    graph.getEdges().add(new GEdge(u, v, weight));
                    edgeCount++;
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid edge weight at edge " + edgeCount + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            graph.clear(); // Rollback on error
            if (e instanceof IOException) {
                throw e;
            }
            throw new IOException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }
}

