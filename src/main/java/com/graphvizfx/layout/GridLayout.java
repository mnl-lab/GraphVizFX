package com.graphvizfx.layout;

import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.GNode;

public class GridLayout implements LayoutAlgorithm {
    
    @Override
    public String getName() {
        return "Grid";
    }
    
    @Override
    public void apply(GraphModel graph, double width, double height) {
        if (graph.getNodes().isEmpty()) {
            return;
        }
        
        int cols = (int) Math.ceil(Math.sqrt(graph.getNodes().size()));
        double gap = 80;
        double startX = (width - cols * gap) / 2 + 40;
        double startY = 60;
        
        for (int i = 0; i < graph.getNodes().size(); i++) {
            GNode n = graph.getNodes().get(i);
            n.setX(startX + (i % cols) * gap);
            n.setY(startY + (i / cols) * gap);
        }
    }
}

