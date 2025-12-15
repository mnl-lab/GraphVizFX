package com.graphvizfx.algorithms;

import com.graphvizfx.TestGraphFactory;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DFSAlgorithmTest {

    @Test
    void depthFirstUsesStackOrderWithReversedNeighbors() {
        // DFS should push neighbors in reverse insertion order to the stack.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B", "C", "D");
        TestGraphFactory.connect(graph, "A", "B", 1);
        TestGraphFactory.connect(graph, "A", "C", 1);
        TestGraphFactory.connect(graph, "B", "D", 1);
        GNode start = graph.getNode("A");

        List<VisualState> history = new DFSAlgorithm().execute(graph, start, null);
        List<String> visitingOrder = extractVisits(history);

        assertEquals(List.of("A", "B", "D", "C"), visitingOrder);
    }

    private List<String> extractVisits(List<VisualState> history) {
        String log = history.get(history.size() - 1).getLogLine();
        return log.lines()
                .filter(line -> line.contains("Visited "))
                .map(line -> line.replaceAll(".*Visited ([^ ]+)$", "$1"))
                .collect(Collectors.toList());
    }
}
