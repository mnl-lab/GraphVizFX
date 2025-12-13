package com.graphvizfx.layout;

import com.graphvizfx.model.GraphModel;

public interface LayoutAlgorithm {
    void apply(GraphModel graph, double width, double height);
    String getName();
}

