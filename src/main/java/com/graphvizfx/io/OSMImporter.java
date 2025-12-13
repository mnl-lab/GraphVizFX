package com.graphvizfx.io;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSMImporter {
    
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
        Map<String, GNode> nodeMap = new HashMap<>();
        
        try {
            Pattern nodePattern = Pattern.compile("<node\\s+id=\"([^\"]+)\"\\s+lat=\"([^\"]+)\"\\s+lon=\"([^\"]+)\"");
            Matcher nodeMatcher = nodePattern.matcher(content);
            
            int nodeCount = 0;
            while (nodeMatcher.find()) {
                try {
                    String id = nodeMatcher.group(1);
                    double lat = Double.parseDouble(nodeMatcher.group(2));
                    double lon = Double.parseDouble(nodeMatcher.group(3));
                    
                    // Validate coordinates
                    if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                        continue; // Skip invalid coordinates
                    }
                    
                    double x = (lon + 180) * 10;
                    double y = (90 - lat) * 10;
                    
                    GNode node = new GNode("N" + id, x, y);
                    nodeMap.put(id, node);
                    graph.getNodes().add(node);
                    nodeCount++;
                } catch (NumberFormatException e) {
                    // Skip malformed node entries
                    continue;
                }
            }
            
            if (nodeMap.isEmpty()) {
                throw new IOException("No valid nodes found in OSM file");
            }
            
            Pattern wayPattern = Pattern.compile("<way\\s+id=\"([^\"]+)\">(.*?)</way>", Pattern.DOTALL);
            Matcher wayMatcher = wayPattern.matcher(content);
            
            int edgeCount = 0;
            while (wayMatcher.find()) {
                Pattern ndPattern = Pattern.compile("<nd\\s+ref=\"([^\"]+)\"");
                Matcher ndMatcher = ndPattern.matcher(wayMatcher.group(2));
                
                GNode prev = null;
                while (ndMatcher.find()) {
                    String refId = ndMatcher.group(1);
                    GNode current = nodeMap.get(refId);
                    
                    // Only create edge if both nodes exist
                    if (current != null && prev != null) {
                        if (!graph.hasEdge(prev, current)) {
                            int weight = (int) Math.hypot(prev.getX() - current.getX(), prev.getY() - current.getY());
                            graph.getEdges().add(new GEdge(prev, current, weight));
                            edgeCount++;
                        }
                    }
                    prev = current;
                }
            }
            
            graph.setDirected(false);
            graph.setWeighted(true);
        } catch (Exception e) {
            graph.clear(); // Rollback on error
            if (e instanceof IOException) {
                throw e;
            }
            throw new IOException("Failed to parse OSM file: " + e.getMessage(), e);
        }
    }
}

