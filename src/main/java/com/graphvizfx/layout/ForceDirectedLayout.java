package com.graphvizfx.layout;

import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;

import java.util.HashMap;
import java.util.Map;

public class ForceDirectedLayout implements LayoutAlgorithm {
    
    @Override
    public String getName() {
        return "Force-Directed";
    }
    
    @Override
    public void apply(GraphModel graph, double width, double height) {
        if (graph.getNodes().isEmpty()) {
            return;
        }
        
        // Validate dimensions
        if (width <= 0 || height <= 0) {
            return;
        }
        
        int iterations = 150;
        double area = width * height;
        double k = Math.sqrt(Math.max(1, area / graph.getNodes().size()));
        double temp = Math.max(1, width / 10);
        
        for (int i = 0; i < iterations; i++) {
            Map<GNode, Double> dispX = new HashMap<>();
            Map<GNode, Double> dispY = new HashMap<>();
            
            for (GNode v : graph.getNodes()) {
                dispX.put(v, 0.0);
                dispY.put(v, 0.0);
                for (GNode u : graph.getNodes()) {
                    if (v != u) {
                        double dx = v.getX() - u.getX();
                        double dy = v.getY() - u.getY();
                        double d = Math.max(1, Math.hypot(dx, dy));
                        double force = (k * k) / d;
                        dispX.put(v, dispX.get(v) + (dx / d) * force);
                        dispY.put(v, dispY.get(v) + (dy / d) * force);
                    }
                }
            }
            
            for (GEdge e : graph.getEdges()) {
                double dx = e.getTarget().getX() - e.getSource().getX();
                double dy = e.getTarget().getY() - e.getSource().getY();
                double d = Math.max(1, Math.hypot(dx, dy));
                double force = (d * d) / k;
                double fx = (dx / d) * force;
                double fy = (dy / d) * force;
                dispX.put(e.getSource(), dispX.get(e.getSource()) + fx);
                dispY.put(e.getSource(), dispY.get(e.getSource()) + fy);
                dispX.put(e.getTarget(), dispX.get(e.getTarget()) - fx);
                dispY.put(e.getTarget(), dispY.get(e.getTarget()) - fy);
            }
            
            for (GNode v : graph.getNodes()) {
                double dx = dispX.get(v);
                double dy = dispY.get(v);
                double d = Math.max(1, Math.hypot(dx, dy));
                double limit = Math.min(d, temp);
                v.setX(v.getX() + (dx / d) * limit);
                v.setY(v.getY() + (dy / d) * limit);
                v.setX(Math.max(v.getRadius() + 5, Math.min(width - v.getRadius() - 5, v.getX())));
                v.setY(Math.max(v.getRadius() + 5, Math.min(height - v.getRadius() - 5, v.getY())));
            }
            temp *= 0.95;
        }
    }
}

