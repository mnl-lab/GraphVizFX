package com.graphvizfx.algorithms;

import com.graphvizfx.TestGraphFactory;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraAlgorithmTest {

    @Test
    void computesWeightedShortestPaths() {
        // Weighted run should relax edges and keep the best cost path.
        GraphModel graph = TestGraphFactory.createGraph(false, true, "A", "B", "C");
        TestGraphFactory.connect(graph, "A", "B", 2);
        TestGraphFactory.connect(graph, "A", "C", 5);
        TestGraphFactory.connect(graph, "B", "C", 1);

        List<VisualState> history = new DijkstraAlgorithm().execute(graph, graph.getNode("A"), null);
        VisualState finalState = history.get(history.size() - 1);

        assertEquals(0.0, finalState.getDistances().get("A"));
        assertEquals(2.0, finalState.getDistances().get("B"));
        assertEquals(3.0, finalState.getDistances().get("C"));
    }

    @Test
    void treatsAllEdgesAsUnitWeightWhenGraphIsUnweighted() {
        // When graph is unweighted, all edges count as distance 1 regardless of weight field.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B", "C");
        TestGraphFactory.connect(graph, "A", "B", 10);
        TestGraphFactory.connect(graph, "A", "C", 5);

        List<VisualState> history = new DijkstraAlgorithm().execute(graph, graph.getNode("A"), null);
        VisualState finalState = history.get(history.size() - 1);

        assertEquals(0.0, finalState.getDistances().get("A"));
        assertEquals(1.0, finalState.getDistances().get("B"));
        assertEquals(1.0, finalState.getDistances().get("C"));
    }

    @Test
    void leavesDisconnectedNodesAtInitialMaxDistance() {
        // Nodes without paths from the start should keep the seeded max value.
        GraphModel graph = TestGraphFactory.createGraph(false, true, "A", "B");
        graph.getNodes().add(new com.graphvizfx.model.GNode("C", 0, 0));

        List<VisualState> history = new DijkstraAlgorithm().execute(graph, graph.getNode("A"), null);
        VisualState finalState = history.get(history.size() - 1);

        assertEquals(Double.MAX_VALUE, finalState.getDistances().get("C"));
    }

    @Test
    void missingStartReturnsLoggedSingleState() {
        // Null start should short-circuit with a descriptive message.
        GraphModel graph = TestGraphFactory.createGraph(false, true, "A", "B");

        List<VisualState> history = new DijkstraAlgorithm().execute(graph, null, null);

        assertEquals(1, history.size());
        assertTrue(history.get(0).getLogLine().contains("Start node not provided"));
    }
}
