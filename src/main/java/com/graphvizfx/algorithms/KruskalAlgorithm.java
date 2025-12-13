package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * Kruskal's algorithm implementation that visualizes sorted edges and cycle checks.
 */
public class KruskalAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "Kruskal";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        VisualState state = history.get(0);
        int step = 1;

        List<GEdge> sorted = new ArrayList<>(graph.getEdges());
        sorted.sort(Comparator.comparingInt(GEdge::getWeight));

        Map<String, String> parent = new HashMap<>();
        for (GNode node : graph.getNodes()) {
            parent.put(node.getId(), node.getId());
        }

        AlgorithmEngine.log(history, state, "Kruskal", step++, "Sorted " + sorted.size() + " edges by weight");

        for (GEdge edge : sorted) {
            state = history.get(history.size() - 1);
            state.getEdgeColors().put(edge.getId(), AlgorithmEngine.EDGE_HIGHLIGHT_COLOR);
            AlgorithmEngine.log(history, state, "Kruskal", step++,
                    String.format("Evaluating %s-%s (w=%d)", edge.getSource().getId(), edge.getTarget().getId(), edge.getWeight()));

            String rootU = find(parent, edge.getSource().getId());
            String rootV = find(parent, edge.getTarget().getId());

            state = history.get(history.size() - 1);
            if (!rootU.equals(rootV)) {
                parent.put(rootU, rootV);
                state.getEdgeColors().put(edge.getId(), AlgorithmEngine.PATH_COLOR);
                state.getNodeColors().put(edge.getSource().getId(), AlgorithmEngine.PATH_COLOR);
                state.getNodeColors().put(edge.getTarget().getId(), AlgorithmEngine.PATH_COLOR);
                AlgorithmEngine.log(history, state, "Kruskal", step++, "Accepted edge (no cycle).");
            } else {
                state.getEdgeColors().put(edge.getId(), AlgorithmEngine.DISCARD_COLOR);
                AlgorithmEngine.log(history, state, "Kruskal", step++, "Rejected edge (cycle detected).");
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "Kruskal", step, "MST construction complete.");
        return history;
    }
    
    private String find(Map<String, String> p, String i) {
        // Iterative path compression to avoid stack overflow
        String root = i;
        while (!p.get(root).equals(root)) {
            root = p.get(root);
        }
        // Path compression
        String current = i;
        while (!p.get(current).equals(root)) {
            String next = p.get(current);
            p.put(current, root);
            current = next;
        }
        return root;
    }
}

