package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * Dijkstra shortest-path implementation that records relaxation events.
 */
public class DijkstraAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "Dijkstra";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        if (start == null) {
            history.get(0).setLogLine("[Dijkstra] Start node not provided." + System.lineSeparator());
            return history;
        }

        VisualState state = history.get(0);
        int step = 1;

        final Map<String, Double> calcDists = new HashMap<>();
        for (GNode node : graph.getNodes()) {
            calcDists.put(node.getId(), Double.MAX_VALUE);
        }
        calcDists.put(start.getId(), 0.0);
        state.getDistances().putAll(calcDists);

        PriorityQueue<GNode> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> calcDists.get(n.getId())));
        queue.add(start);
        Set<String> settled = new HashSet<>();

        AlgorithmEngine.log(history, state, "Dijkstra", step++, "Initialized start node " + start.getId());

        while (!queue.isEmpty()) {
            state = history.get(history.size() - 1);
            GNode current = queue.poll();

            if (settled.contains(current.getId())) {
                continue;
            }
            settled.add(current.getId());
            state.getNodeColors().put(current.getId(), AlgorithmEngine.VISITED_COLOR);
            AlgorithmEngine.log(history, state, "Dijkstra", step++,
                    String.format("Settled %s (dist = %.2f)", current.getId(), calcDists.get(current.getId())));

            for (GEdge edge : graph.getEdges()) {
                GNode neighbor = null;
                if (edge.getSource() == current) {
                    neighbor = edge.getTarget();
                } else if (!graph.isDirected() && edge.getTarget() == current) {
                    neighbor = edge.getSource();
                }

                if (neighbor != null && !settled.contains(neighbor.getId())) {
                    double previousDist = calcDists.get(neighbor.getId());
                    double candidateDist = calcDists.get(current.getId()) + (graph.isWeighted() ? edge.getWeight() : 1);

                    if (candidateDist < previousDist) {
                        calcDists.put(neighbor.getId(), candidateDist);

                        state = history.get(history.size() - 1);
                        state.getDistances().put(neighbor.getId(), candidateDist);
                        state.getNodeColors().put(neighbor.getId(), AlgorithmEngine.FRONTIER_COLOR);
                        state.getEdgeColors().put(edge.getId(), AlgorithmEngine.ACTIVE_EDGE_COLOR);

                        queue.add(neighbor);
                        AlgorithmEngine.log(history, state, "Dijkstra", step++,
                                String.format("Relaxed %s→%s (%s → %.2f)", current.getId(), neighbor.getId(),
                                        formatDistance(previousDist), candidateDist));
                    }
                }
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "Dijkstra", step, "Finished shortest paths.");
        return history;
    }

    private String formatDistance(double value) {
        return Double.isInfinite(value) || value == Double.MAX_VALUE ? "∞" : String.format("%.2f", value);
    }
}

