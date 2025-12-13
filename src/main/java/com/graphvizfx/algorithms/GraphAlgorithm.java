package com.graphvizfx.algorithms;

import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;

import java.util.List;

/**
 * Contract for algorithm executors that return a step-by-step visual trace.
 */
public interface GraphAlgorithm {

    /**
     * Executes the graph algorithm using the provided start/goal constraints.
     *
     * @param graph model to operate on
     * @param start optional starting node (null when not required)
     * @param goal  optional goal/destination node (null when not required)
     * @return ordered list of {@link VisualState} instances representing each step
     */
    List<VisualState> execute(GraphModel graph, GNode start, GNode goal);

    /**
     * @return the human readable algorithm name
     */
    String getName();
}

