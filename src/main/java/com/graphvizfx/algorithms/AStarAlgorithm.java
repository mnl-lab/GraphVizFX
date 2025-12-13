package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * A* search visualizer that tracks open/closed sets and highlights the final path.
 */
public class AStarAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "A*";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        if (start == null || goal == null) {
            history.get(0).setLogLine("[A*] Start and goal nodes are required." + System.lineSeparator());
            return history;
        }

        VisualState state = history.get(0);
        int step = 1;

        if (start.getId().equals(goal.getId())) {
            state.getNodeColors().put(start.getId(), AlgorithmEngine.GOAL_COLOR);
            AlgorithmEngine.log(history, state, "A*", step, "Start and goal are the same node " + start.getId());
            return history;
        }

        state.getNodeColors().put(start.getId(), AlgorithmEngine.FRONTIER_COLOR);
        state.getNodeColors().put(goal.getId(), AlgorithmEngine.GOAL_COLOR);
        AlgorithmEngine.log(history, state, "A*", step++,
                String.format("Searching path from %s to %s", start.getId(), goal.getId()));

        final Map<String, Double> gScore = new HashMap<>();
        for (GNode node : graph.getNodes()) {
            gScore.put(node.getId(), Double.MAX_VALUE);
        }
        gScore.put(start.getId(), 0.0);
        state.getDistances().putAll(gScore);

        Map<String, String> parentNode = new HashMap<>();
        Map<String, String> parentEdge = new HashMap<>();

        PriorityQueue<GNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(
                node -> gScore.get(node.getId()) + heuristic(node, goal)));
        openSet.add(start);
        Set<String> closed = new HashSet<>();

        while (!openSet.isEmpty()) {
            state = history.get(history.size() - 1);
            GNode current = openSet.poll();

            if (current.getId().equals(goal.getId())) {
                state.getNodeColors().put(current.getId(), AlgorithmEngine.GOAL_COLOR);
                highlightPath(state, parentNode, parentEdge, start, goal);
                AlgorithmEngine.log(history, state, "A*", step++, "Goal reached. Path highlighted.");
                return history;
            }

            if (!closed.add(current.getId())) {
                continue;
            }

            state.getNodeColors().put(current.getId(), AlgorithmEngine.VISITED_COLOR);
            AlgorithmEngine.log(history, state, "A*", step++,
                    String.format("Expanded %s (g = %.2f)", current.getId(), gScore.get(current.getId())));

            for (GEdge edge : graph.getEdges()) {
                GNode neighbor = null;
                if (edge.getSource() == current) {
                    neighbor = edge.getTarget();
                } else if (!graph.isDirected() && edge.getTarget() == current) {
                    neighbor = edge.getSource();
                }

                if (neighbor != null && !closed.contains(neighbor.getId())) {
                    double tentative = gScore.get(current.getId()) + (graph.isWeighted() ? edge.getWeight() : 1);
                    if (tentative < gScore.get(neighbor.getId())) {
                        gScore.put(neighbor.getId(), tentative);
                        state = history.get(history.size() - 1);
                        state.getDistances().put(neighbor.getId(), tentative);
                        state.getNodeColors().put(neighbor.getId(), AlgorithmEngine.FRONTIER_COLOR);
                        state.getEdgeColors().put(edge.getId(), AlgorithmEngine.ACTIVE_EDGE_COLOR);

                        parentNode.put(neighbor.getId(), current.getId());
                        parentEdge.put(neighbor.getId(), edge.getId());

                        openSet.add(neighbor);
                        AlgorithmEngine.log(history, state, "A*", step++,
                                String.format("Updated %s via %s (g = %.2f)", neighbor.getId(), current.getId(), tentative));
                    }
                }
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "A*", step, "Goal unreachable.");
        return history;
    }

    private double heuristic(GNode node, GNode goal) {
        return Math.hypot(node.getX() - goal.getX(), node.getY() - goal.getY());
    }

    private void highlightPath(VisualState state, Map<String, String> parentNode, Map<String, String> parentEdge,
                               GNode start, GNode goal) {
        String cursor = goal.getId();
        state.getNodeColors().put(cursor, AlgorithmEngine.GOAL_COLOR);
        while (parentNode.containsKey(cursor)) {
            String edgeId = parentEdge.get(cursor);
            if (edgeId != null) {
                state.getEdgeColors().put(edgeId, AlgorithmEngine.PATH_COLOR);
            }
            cursor = parentNode.get(cursor);
                state.getNodeColors().put(cursor, AlgorithmEngine.PATH_COLOR);
            if (cursor.equals(start.getId())) {
                break;
            }
        }
    }
}

