package com.graphvizfx.layout;

import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.GNode;

public class CircularLayout implements LayoutAlgorithm {
    
    @Override
    public String getName() {
        return "Circular";
    }
    
    @Override
    public void apply(GraphModel graph, double width, double height) {
        if (graph.getNodes().isEmpty()) {
            return;
        }
        
        double cx = width / 2;
        double cy = height / 2;
        double r = Math.min(width, height) / 2.5;
        
        for (int i = 0; i < graph.getNodes().size(); i++) {
            double ang = 2 * Math.PI * i / graph.getNodes().size();
            GNode n = graph.getNodes().get(i);
            n.setX(cx + r * Math.cos(ang));
            n.setY(cy + r * Math.sin(ang));
        }
    }
}

