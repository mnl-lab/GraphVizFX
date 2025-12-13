package com.graphvizfx.algorithms;

import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import java.util.*;

/**
 * Prim's minimum spanning tree builder with clear edge and node highlighting.
 */
public class PrimAlgorithm implements GraphAlgorithm {
    
    @Override
    public String getName() {
        return "Prim";
    }
    
    @Override
    public List<VisualState> execute(GraphModel graph, GNode start, GNode goal) {
        List<VisualState> history = AlgorithmEngine.prepareTrace();
        if (start == null) {
            history.get(0).setLogLine("[Prim] Start node not provided." + System.lineSeparator());
            return history;
        }

        VisualState state = history.get(0);
        int step = 1;

        Set<String> mstNodes = new HashSet<>();
        PriorityQueue<GEdge> queue = new PriorityQueue<>(Comparator.comparingInt(GEdge::getWeight));

        mstNodes.add(start.getId());
        state.getNodeColors().put(start.getId(), AlgorithmEngine.PATH_COLOR);
        queue.addAll(adjacentEdges(graph, start));
        AlgorithmEngine.log(history, state, "Prim", step++, "Seeded PQ with edges from " + start.getId());

        while (!queue.isEmpty()) {
            state = history.get(history.size() - 1);
            GEdge candidate = queue.poll();
            GNode u = candidate.getSource();
            GNode v = candidate.getTarget();

            if (mstNodes.contains(u.getId()) && mstNodes.contains(v.getId())) {
                continue;
            }

            GNode next = mstNodes.contains(u.getId()) ? v : u;
            mstNodes.add(next.getId());

            state.getEdgeColors().put(candidate.getId(), AlgorithmEngine.PATH_COLOR);
            state.getNodeColors().put(next.getId(), AlgorithmEngine.PATH_COLOR);
            AlgorithmEngine.log(history, state, "Prim", step++,
                    String.format("Added %s via %s-%s (w=%d)", next.getId(), u.getId(), v.getId(), candidate.getWeight()));

            for (GEdge edge : adjacentEdges(graph, next)) {
                if (!mstNodes.contains(edge.getSource().getId()) || !mstNodes.contains(edge.getTarget().getId())) {
                    queue.add(edge);
                    VisualState latest = history.get(history.size() - 1);
                    latest.getEdgeColors().put(edge.getId(), AlgorithmEngine.EDGE_HIGHLIGHT_COLOR);
                }
            }
        }

        AlgorithmEngine.log(history, history.get(history.size() - 1), "Prim", step, "MST construction complete.");
        return history;
    }

    private List<GEdge> adjacentEdges(GraphModel graph, GNode node) {
        List<GEdge> edges = new ArrayList<>();
        for (GEdge edge : graph.getEdges()) {
            if (edge.getSource() == node || edge.getTarget() == node) {
                edges.add(edge);
            }
        }
        return edges;
    }
}

