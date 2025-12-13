package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * Depth-first traversal trace that highlights stack operations per step.
 */
public class DFSAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "DFS";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        if (start == null) {
            history.get(0).setLogLine("[DFS] Start node not provided." + System.lineSeparator());
            return history;
        }

        VisualState state = history.get(0);
        int step = 1;

        Deque<GNode> stack = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        stack.push(start);
        state.getNodeColors().put(start.getId(), AlgorithmEngine.FRONTIER_COLOR);
        AlgorithmEngine.log(history, state, "DFS", step++, "Pushed start node " + start.getId() + " onto stack");

        while (!stack.isEmpty()) {
            state = history.get(history.size() - 1);
            GNode current = stack.pop();

            if (visited.contains(current.getId())) {
                AlgorithmEngine.log(history, state, "DFS", step++,
                        "Skipped " + current.getId() + " because it was already visited");
                continue;
            }

            visited.add(current.getId());
            state.getNodeColors().put(current.getId(), AlgorithmEngine.VISITED_COLOR);
            AlgorithmEngine.log(history, state, "DFS", step++, "Visited " + current.getId());

            List<GEdge> neighborEdges = new ArrayList<>();
            for (GEdge edge : graph.getEdges()) {
                if (edge.getSource() == current) {
                    neighborEdges.add(edge);
                } else if (!graph.isDirected() && edge.getTarget() == current) {
                    neighborEdges.add(edge);
                }
            }
            Collections.reverse(neighborEdges);

            for (GEdge edge : neighborEdges) {
                GNode neighbor = edge.getSource() == current ? edge.getTarget() : edge.getSource();
                if (!visited.contains(neighbor.getId())) {
                    stack.push(neighbor);
                    state = history.get(history.size() - 1);
                    state.getNodeColors().put(neighbor.getId(), AlgorithmEngine.FRONTIER_COLOR);
                    state.getEdgeColors().put(edge.getId(), AlgorithmEngine.ACTIVE_EDGE_COLOR);
                    AlgorithmEngine.log(history, state, "DFS", step++,
                            String.format("Added %s via edge %s-%s", neighbor.getId(),
                                    edge.getSource().getId(), edge.getTarget().getId()));
                }
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "DFS", step, "Traversal complete.");
        return history;
    }
}

