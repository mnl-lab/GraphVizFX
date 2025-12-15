package com.graphvizfx.algorithms;

import com.graphvizfx.TestGraphFactory;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BFSAlgorithmTest {

    @Test
    void breadthFirstVisitsInQueueOrderForUndirectedGraph() {
        // Verify BFS expands neighbors in insertion order when graph is undirected.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A", "B", "C", "D");
        TestGraphFactory.connect(graph, "A", "B", 1);
        TestGraphFactory.connect(graph, "A", "C", 1);
        TestGraphFactory.connect(graph, "B", "D", 1);
        GNode start = graph.getNode("A");

        List<VisualState> history = new BFSAlgorithm().execute(graph, start, null);
        List<String> visitingOrder = extractVisits(history);

        assertEquals(List.of("A", "B", "C", "D"), visitingOrder);
    }

    @Test
    void missingStartProducesSingleLoggedState() {
        // When start is null, algorithm should short-circuit with an explanatory log.
        GraphModel graph = TestGraphFactory.createGraph(false, false, "A");

        List<VisualState> history = new BFSAlgorithm().execute(graph, null, null);

        assertEquals(1, history.size());
        assertTrue(history.get(0).getLogLine().contains("Start node not provided"));
    }

    private List<String> extractVisits(List<VisualState> history) {
        String log = history.get(history.size() - 1).getLogLine();
        return log.lines()
                .filter(line -> line.contains("Visiting"))
                .map(line -> line.replaceAll(".*Visiting ([^ ]+).*", "$1"))
                .collect(Collectors.toList());
    }
}
