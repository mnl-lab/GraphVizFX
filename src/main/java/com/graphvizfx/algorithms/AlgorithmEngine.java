package com.graphvizfx.algorithms;

import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for executing graph algorithms and providing shared visualization helpers.
 */
public final class AlgorithmEngine {

    /** Color for nodes currently in the frontier/open set. */
    public static final Color FRONTIER_COLOR = Color.web("#ffbe0b");
    /** Color for nodes that have been processed/closed. */
    public static final Color VISITED_COLOR = Color.web("#3a86ff");
    /** Color for final paths or accepted tree edges. */
    public static final Color PATH_COLOR = Color.web("#06d6a0");
    /** Color used when highlighting candidate edges (e.g., MST checks). */
    public static final Color EDGE_HIGHLIGHT_COLOR = Color.web("#ff006e");
    /** Color for actively explored edges during relaxations/expansions. */
    public static final Color ACTIVE_EDGE_COLOR = Color.web("#ff9f1c");
    /** Goal/target highlight color. */
    public static final Color GOAL_COLOR = Color.web("#ef476f");
    /** Neutral gray used for discarded edges. */
    public static final Color DISCARD_COLOR = Color.web("#adb5bd");

    private AlgorithmEngine() {
        // Utility class
    }

    /**
     * Executes the specified algorithm on the given graph.
     *
     * @param algo  The algorithm name (BFS, DFS, Dijkstra, A*, Prim, Kruskal)
     * @param graph The graph model to execute the algorithm on
     * @param start The starting node (may be null for Kruskal)
     * @param goal  Optional goal node (only used by A*)
     * @return List of visual states representing algorithm execution steps
     * @throws IllegalArgumentException if algorithm name is invalid or graph is invalid
     */
    public static List<VisualState> execute(String algo, GraphModel graph, GNode start, GNode goal) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }

        if (graph.getNodes().isEmpty()) {
            List<VisualState> empty = prepareTrace();
            empty.get(0).setLogLine("Error: Graph is empty." + System.lineSeparator());
            return empty;
        }

        GraphAlgorithm algorithm = createAlgorithm(algo);
        if (algorithm == null) {
            throw new IllegalArgumentException("Unknown algorithm: " + algo);
        }

        return algorithm.execute(graph, start, goal);
    }

    /**
     * Creates the shared trace structure (initial empty visual state).
     */
    public static List<VisualState> prepareTrace() {
        List<VisualState> history = new ArrayList<>();
        VisualState seed = new VisualState();
        seed.setLogLine("");
        history.add(seed);
        return history;
    }

    private static GraphAlgorithm createAlgorithm(String algo) {
        switch (algo) {
            case "BFS":
                return new BFSAlgorithm();
            case "DFS":
                return new DFSAlgorithm();
            case "Dijkstra":
                return new DijkstraAlgorithm();
            case "A*":
                return new AStarAlgorithm();
            case "Prim":
                return new PrimAlgorithm();
            case "Kruskal":
                return new KruskalAlgorithm();
            default:
                return null;
        }
    }

    /**
     * Appends a new visual state snapshot with an updated log line.
     */
    static void log(List<VisualState> trace, VisualState current, String algo, int step, String msg) {
        VisualState snapshot = current.copy();
        String existing = snapshot.getLogLine() == null ? "" : snapshot.getLogLine();
        snapshot.setLogLine(existing + String.format("[%s] Step %d: %s%s", algo, step, msg, System.lineSeparator()));
        trace.add(snapshot);
    }
}

