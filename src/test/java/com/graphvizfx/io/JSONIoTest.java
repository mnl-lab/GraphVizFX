package com.graphvizfx.io;

import com.graphvizfx.TestGraphFactory;
import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JSONIoTest {

    @Test
    void exportThenImportPreservesStructureAndFlags(@TempDir Path tempDir) throws Exception {
        // Round-trip should keep directed/weighted flags, nodes, and edge weights.
        GraphModel original = TestGraphFactory.createGraph(true, true, "A", "B");
        GEdge edge = TestGraphFactory.connect(original, "A", "B", 7);
        GNode a = original.getNode("A");
        a.setX(12.5);
        a.setY(3.0);

        File file = tempDir.resolve("graph.json").toFile();
        JSONExporter.export(original, file);

        GraphModel restored = new GraphModel();
        JSONImporter.importGraph(restored, file);

        assertTrue(restored.isDirected());
        assertTrue(restored.isWeighted());
        assertEquals(2, restored.getNodes().size());
        assertEquals(1, restored.getEdges().size());
        assertEquals("A", restored.getEdges().get(0).getSource().getId());
        assertEquals("B", restored.getEdges().get(0).getTarget().getId());
        assertEquals(edge.getWeight(), restored.getEdges().get(0).getWeight());
        assertEquals(12.5, restored.getNode("A").getX());
        assertEquals(3.0, restored.getNode("A").getY());
    }

    @Test
    void importFailsOnEmptyFile(@TempDir Path tempDir) throws IOException {
        // Importing an empty file should signal invalid input instead of mutating the graph.
        Path file = tempDir.resolve("empty.json");
        Files.createFile(file);
        GraphModel graph = new GraphModel();

        IOException ex = assertThrows(IOException.class, () -> JSONImporter.importGraph(graph, file.toFile()));
        assertTrue(ex.getMessage().toLowerCase().contains("empty"));
        assertTrue(graph.getNodes().isEmpty());
        assertTrue(graph.getEdges().isEmpty());
    }
}
