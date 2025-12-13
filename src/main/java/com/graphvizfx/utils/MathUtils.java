package com.graphvizfx.utils;

import com.graphvizfx.model.GNode;

public class MathUtils {
    
    public static double dist(GNode a, GNode b) {
        return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
    }
    
    public static double ptSegDist(double x1, double y1, double x2, double y2, double px, double py) {
        double dx = x2 - x1, dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        return Math.hypot(px - (x1 + t * dx), py - (y1 + t * dy));
    }
}

