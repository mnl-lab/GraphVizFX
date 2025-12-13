package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * Breadth-first traversal that emits visuals for queue/frontier operations.
 */
public class BFSAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "BFS";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        if (start == null) {
            history.get(0).setLogLine("[BFS] Start node not provided." + System.lineSeparator());
            return history;
        }

        VisualState state = history.get(0);
        int step = 1;

        Queue<GNode> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();

        queue.add(start);
        visited.add(start.getId());
        state.getNodeColors().put(start.getId(), AlgorithmEngine.FRONTIER_COLOR);
        AlgorithmEngine.log(history, state, "BFS", step++, "Enqueued start node " + start.getId());

        while (!queue.isEmpty()) {
            state = history.get(history.size() - 1);
            GNode current = queue.poll();
            state.getNodeColors().put(current.getId(), AlgorithmEngine.VISITED_COLOR);
            AlgorithmEngine.log(history, state, "BFS", step++, "Visiting " + current.getId() + " and scanning neighbors");

            for (GEdge edge : graph.getEdges()) {
                GNode neighbor = null;
                if (edge.getSource() == current) {
                    neighbor = edge.getTarget();
                } else if (!graph.isDirected() && edge.getTarget() == current) {
                    neighbor = edge.getSource();
                }

                if (neighbor != null && !visited.contains(neighbor.getId())) {
                    state = history.get(history.size() - 1);
                    visited.add(neighbor.getId());
                    queue.add(neighbor);
                    state.getNodeColors().put(neighbor.getId(), AlgorithmEngine.FRONTIER_COLOR);
                    state.getEdgeColors().put(edge.getId(), AlgorithmEngine.ACTIVE_EDGE_COLOR);
                    AlgorithmEngine.log(history, state, "BFS", step++,
                            String.format("Discovered %s from %s", neighbor.getId(), current.getId()));
                }
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "BFS", step, "Traversal complete.");
        return history;
    }
}

