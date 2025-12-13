package com.graphvizfx.app;

import com.graphvizfx.controller.GraphController;
import com.graphvizfx.io.ImageExporter;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.VisualState;
import com.graphvizfx.view.GraphCanvas;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JavaFX entry point that wires together the controller, canvases, and UI chrome.
 */
public class GraphVizFX extends Application {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------
    private GraphController controller;
    private Stage primaryStage;
    private BorderPane root;
    private GraphCanvas mainCanvas;
    private GraphCanvas compareCanvas;
    private TextArea logArea;
    private Label statusLabel;
    private Button btnPrev;
    private Button btnNext;
    private int currentStep = 0;
    private boolean isComparisonMode = false;
    private List<VisualState> tracePrimary = new ArrayList<>();
    private List<VisualState> traceSecondary = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        controller = new GraphController();

        root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-background-color: #f4f6f9;");

        mainCanvas = new GraphCanvas(controller, true);
        root.setCenter(mainCanvas);
        root.setTop(createToolbar());
        root.setRight(createSidebar());
        root.setBottom(createLogPanel());

        Scene scene = new Scene(root, 1320, 820);
        stage.setTitle("GraphVizFX");
        stage.setScene(scene);
        stage.show();

        resetVisuals();
    }

    // -------------------------------------------------------------------------
    // UI setup
    // -------------------------------------------------------------------------

    private ToolBar createToolbar() {
        Button btnNew = new Button("New Graph");
        btnNew.setOnAction(e -> resetGraph());

        Button btnResetView = new Button("Reset Visuals");
        btnResetView.setOnAction(e -> resetVisuals());

        Separator sep1 = new Separator();

        ComboBox<String> layoutBox = new ComboBox<>(FXCollections.observableArrayList("Force-Directed", "Circular", "Grid"));
        layoutBox.setPromptText("Layout");
        layoutBox.setOnAction(e -> applyLayout(layoutBox.getValue()));

        ToggleButton tDir = new ToggleButton("Directed");
        tDir.selectedProperty().addListener((o, old, val) -> {
            controller.getGraph().setDirected(val);
            mainCanvas.draw();
        });

        ToggleButton tWeight = new ToggleButton("Weighted");
        tWeight.selectedProperty().addListener((o, old, val) -> {
            controller.getGraph().setWeighted(val);
            mainCanvas.draw();
        });

        Separator sep2 = new Separator();

        Button btnImport = new Button("Import JSON");
        btnImport.setOnAction(e -> importJSON());

        Button btnExport = new Button("Export JSON");
        btnExport.setOnAction(e -> exportJSON());

        Button btnImage = new Button("Export Image");
        btnImage.setOnAction(e -> exportImage());

        return new ToolBar(btnNew, btnResetView, sep1, new Label("Layout:"), layoutBox, new Label("Mode:"), tDir, tWeight, sep2, btnImport, btnExport, btnImage);
    }

    private VBox createSidebar() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(20));
        box.setPrefWidth(320);
        box.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #eff3f8); -fx-border-color: #dbe2ea; -fx-border-width: 0 0 0 1;");

        Label title = new Label("Algorithm Console");
        title.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));

        ComboBox<String> algoChoice = new ComboBox<>(FXCollections.observableArrayList("BFS", "DFS", "Dijkstra", "A*", "Prim", "Kruskal"));
        algoChoice.getSelectionModel().selectFirst();
        algoChoice.setMaxWidth(Double.MAX_VALUE);

        HBox actionBtns = new HBox(10);
        Button btnRun = styledActionButton("Run Once", "#2b8a3e");
        Button btnCompare = styledActionButton("Compare", "#1f6f8b");
        btnRun.setOnAction(e -> runSingleAlgo(algoChoice.getValue()));
        btnCompare.setOnAction(e -> runComparison(algoChoice.getValue()));
        HBox.setHgrow(btnRun, Priority.ALWAYS);
        HBox.setHgrow(btnCompare, Priority.ALWAYS);
        actionBtns.getChildren().addAll(btnRun, btnCompare);

        Label playback = new Label("Step Playback");
        playback.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));

        btnPrev = navigationButton("◀  Previous Step");
        btnNext = navigationButton("Next Step  ▶");
        btnPrev.setOnAction(e -> step(-1));
        btnNext.setOnAction(e -> step(1));

        VBox navBox = new VBox(10, btnPrev, btnNext);

        Label helper = new Label("Navigate each algorithm frame manually. This avoids janky auto animations and keeps rendering stable.");
        helper.setWrapText(true);
        helper.setStyle("-fx-text-fill: #5c677d;");

        box.getChildren().addAll(title, algoChoice, actionBtns, new Separator(), playback, navBox, helper);
        return box;
    }

    private VBox createLogPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(16, 22, 18, 22));
        panel.setStyle("-fx-background-color: #10141c; -fx-border-color: #00000040; -fx-border-width: 1 0 0 0;");

        Label logLabel = new Label("Execution Log");
        logLabel.setStyle("-fx-text-fill: #f8f9fa; -fx-font-size: 14px; -fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefRowCount(6);
        logArea.setStyle("-fx-font-family: 'JetBrains Mono', monospace; -fx-font-size: 12px; -fx-control-inner-background: #0b111a; -fx-text-fill: #cfd8dc;");

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #ced4da;");

        panel.getChildren().addAll(logLabel, logArea, statusLabel);
        return panel;
    }

    private Button styledActionButton(String text, String baseColor) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12 16; -fx-background-radius: 12;",
                baseColor));
        btn.setPrefHeight(48);
        return btn;
    }

    private Button navigationButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(52);
        btn.setStyle("-fx-background-color: #151d2b; -fx-text-fill: #f1f3f5; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 14;");
        return btn;
    }

    // -------------------------------------------------------------------------
    // Core interaction logic
    // -------------------------------------------------------------------------

    private void resetGraph() {
        controller.resetGraph();
        isComparisonMode = false;
        root.setCenter(mainCanvas);
        compareCanvas = null;
        resetVisuals();
        statusLabel.setText("New graph created.");
    }

    private void resetVisuals() {
        currentStep = 0;
        tracePrimary.clear();
        traceSecondary.clear();
        logArea.clear();
        mainCanvas.setCurrentState(null);
        mainCanvas.draw();
        if (compareCanvas != null) {
            compareCanvas.setCurrentState(null);
            compareCanvas.draw();
        }
        refreshNavigationButtons();
        statusLabel.setText("Visuals reset.");
    }

    private void runSingleAlgo(String algo) {
        if (algo == null || !validateAlgo(algo)) {
            return;
        }

        Optional<AlgorithmInput> input = requestInputFor(algo);
        if (input.isEmpty()) {
            return;
        }

        isComparisonMode = false;
        root.setCenter(mainCanvas);
        compareCanvas = null;
        resetVisuals();

        try {
            tracePrimary = controller.executeAlgorithm(algo, input.get().start(), input.get().goal());
            if (tracePrimary.isEmpty()) {
                statusLabel.setText(algo + " failed: No steps generated.");
                refreshNavigationButtons();
                return;
            }
            statusLabel.setText(algo + " completed. Steps: " + tracePrimary.size());
            currentStep = 0;
            updateView();
        } catch (IllegalArgumentException e) {
            showError("Algorithm Error", e.getMessage());
        } catch (Exception e) {
            showError("Algorithm Error", "Unexpected error: " + e.getMessage());
        }
    }

    private void runComparison(String algo1) {
        if (algo1 == null || !validateAlgo(algo1)) {
            return;
        }
        if (controller.getGraph().getNodes().isEmpty()) {
            showError("Comparison Error", "Graph is empty.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("BFS", "BFS", "DFS", "Dijkstra", "A*", "Prim", "Kruskal");
        dialog.setTitle("Select Second Algorithm");
        dialog.setHeaderText("Compare " + algo1 + " with:");
        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) {
            return;
        }
        String algo2 = res.get();
        if (algo2 == null || !validateAlgo(algo2)) {
            return;
        }

        Optional<AlgorithmInput> primaryInput = requestInputFor(algo1);
        if (primaryInput.isEmpty()) {
            return;
        }
        Optional<AlgorithmInput> secondaryInput = requestInputFor(algo2);
        if (secondaryInput.isEmpty()) {
            return;
        }

        isComparisonMode = true;
        compareCanvas = new GraphCanvas(controller, false);
        SplitPane splitPane = new SplitPane(mainCanvas, compareCanvas);
        splitPane.setDividerPositions(0.5);
        root.setCenter(splitPane);
        resetVisuals();

        try {
            tracePrimary = controller.executeAlgorithm(algo1, primaryInput.get().start(), primaryInput.get().goal());
            traceSecondary = controller.executeAlgorithm(algo2, secondaryInput.get().start(), secondaryInput.get().goal());

            if (tracePrimary.isEmpty() || traceSecondary.isEmpty()) {
                showError("Comparison Error", "One or both algorithms failed to execute.");
                refreshNavigationButtons();
                return;
            }

            statusLabel.setText("Comparing " + algo1 + " vs " + algo2);
            currentStep = 0;
            updateView();
        } catch (IllegalArgumentException e) {
            showError("Comparison Error", e.getMessage());
        } catch (Exception e) {
            showError("Comparison Error", "Unexpected error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Algorithm execution & inputs
    // -------------------------------------------------------------------------

    private boolean validateAlgo(String algo) {
        if (algo == null) {
            showError("Validation", "Algorithm name cannot be null.");
            return false;
        }
        
        if (controller.getGraph().getNodes().isEmpty()) {
            showError("Validation", "Graph is empty.");
            return false;
        }
        
        // Validate MST algorithms require undirected graphs
        if (algo.equals("Prim") || algo.equals("Kruskal")) {
            if (controller.getGraph().isDirected()) {
                showError("Validation", algo + " requires an UNDIRECTED graph. Please disable 'Directed' mode.");
                return false;
            }
        }
        
        return true;
    }

    private Optional<AlgorithmInput> requestInputFor(String algorithm) {
        if ("Kruskal".equals(algorithm)) {
            return Optional.of(new AlgorithmInput(null, null));
        }

        if (controller.getGraph().getNodes().isEmpty()) {
            showError("Input Required", "Graph is empty.");
            return Optional.empty();
        }

        Optional<GNode> start = selectNode("Select Start Node",
                "Choose the starting node for " + algorithm + ":", null);
        if (start.isEmpty()) {
            return Optional.empty();
        }

        if ("A*".equals(algorithm)) {
            Optional<GNode> goal = selectNode("Select Destination Node",
                    "Choose the destination node for A*:", start.get().getId());
            return goal.map(g -> new AlgorithmInput(start.get(), g));
        }

        return Optional.of(new AlgorithmInput(start.get(), null));
    }

    private Optional<GNode> selectNode(String title, String header, String excludeId) {
        List<String> ids = controller.getGraph().getNodes().stream()
                .map(GNode::getId)
                .filter(id -> excludeId == null || !excludeId.equals(id))
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            showError("Input Required", "No eligible nodes available for selection.");
            return Optional.empty();
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(ids.get(0), ids);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        return dialog.showAndWait().map(controller.getGraph()::getNode);
    }

    // -------------------------------------------------------------------------
    // Playback & visualization helpers
    // -------------------------------------------------------------------------

    private int getMaxSteps() {
        return Math.max(tracePrimary.size(), traceSecondary.size());
    }

    private void step(int dir) {
        int max = getMaxSteps();
        if (max == 0) {
            return;
        }

        currentStep = Math.min(Math.max(currentStep + dir, 0), max - 1);
        updateView();
    }

    private void updateView() {
        StringBuilder logBuilder = new StringBuilder();

        if (!tracePrimary.isEmpty()) {
            int idx = Math.min(currentStep, tracePrimary.size() - 1);
            VisualState state = tracePrimary.get(idx);
            mainCanvas.setCurrentState(state);
            if (state != null && state.getLogLine() != null) {
                logBuilder.append(state.getLogLine());
            }
        }
        mainCanvas.draw();

        if (isComparisonMode && !traceSecondary.isEmpty()) {
            int idx = Math.min(currentStep, traceSecondary.size() - 1);
            VisualState state = traceSecondary.get(idx);
            compareCanvas.setCurrentState(state);
            logBuilder.append("\n\n----------------\n\n");
            if (state != null && state.getLogLine() != null) {
                logBuilder.append(state.getLogLine());
            }
            compareCanvas.draw();
        }

        logArea.setText(logBuilder.toString());
        logArea.positionCaret(logArea.getText().length());
        logArea.setScrollTop(Double.MAX_VALUE);
        refreshNavigationButtons();
    }

    private void refreshNavigationButtons() {
        if (btnPrev == null || btnNext == null) {
            return;
        }
        int max = getMaxSteps();
        boolean hasTrace = max > 0;
        btnPrev.setDisable(!hasTrace || currentStep == 0);
        btnNext.setDisable(!hasTrace || currentStep >= max - 1);
    }

    // -------------------------------------------------------------------------
    // Layout & persistence
    // -------------------------------------------------------------------------

    private void applyLayout(String type) {
        if (type == null) return;
        
        double width = mainCanvas.getWidth();
        double height = mainCanvas.getHeight();
        
        if (width <= 0 || height <= 0) {
            statusLabel.setText("Cannot apply layout: Canvas size is invalid.");
            return;
        }
        
        try {
            controller.applyLayout(type, width, height);
            mainCanvas.draw();
            if (isComparisonMode && compareCanvas != null) {
                compareCanvas.draw();
            }
            statusLabel.setText("Layout '" + type + "' applied successfully.");
        } catch (Exception e) {
            showError("Layout Error", "Failed to apply layout: " + e.getMessage());
        }
    }

    private void exportJSON() {
        if (controller.getGraph().getNodes().isEmpty()) {
            showError("Export Failed", "Cannot export empty graph.");
            return;
        }
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Graph as JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = fc.showSaveDialog(primaryStage);
        if (f == null) return;

        try {
            controller.exportJSON(f);
            statusLabel.setText("Graph exported successfully to: " + f.getName());
        } catch (IOException e) {
            showError("Export Failed", "Failed to write file: " + e.getMessage());
        } catch (Exception e) {
            showError("Export Failed", "Unexpected error: " + e.getMessage());
        }
    }

    private void importJSON() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Graph from JSON");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = fc.showOpenDialog(primaryStage);
        if (f == null) return;

        try {
            controller.importJSON(f);
            mainCanvas.draw();
            int nodeCount = controller.getGraph().getNodes().size();
            int edgeCount = controller.getGraph().getEdges().size();
            statusLabel.setText(String.format("Graph imported successfully: %d nodes, %d edges.", nodeCount, edgeCount));
        } catch (IOException e) {
            showError("Import Failed", "Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            showError("Import Failed", "Malformed JSON or file error: " + e.getMessage());
        }
    }

    private void exportImage() {
        if (controller.getGraph().getNodes().isEmpty()) {
            showError("Export Failed", "Cannot export empty graph.");
            return;
        }
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Graph as Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Images", "*.png"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = fc.showSaveDialog(primaryStage);
        if (f == null) return;

        File target = f.getAbsolutePath().toLowerCase().endsWith(".png") ? f : new File(f.getAbsolutePath() + ".png");

        try {
            ImageExporter.export(mainCanvas, target);
            statusLabel.setText("Image exported successfully to: " + target.getName());
        } catch (IOException e) {
            showError("Export Failed", "Failed to write image: " + e.getMessage());
        } catch (Exception e) {
            showError("Export Failed", "Unexpected error: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // -------------------------------------------------------------------------
    // Inner data structures
    // -------------------------------------------------------------------------
    private record AlgorithmInput(GNode start, GNode goal) {}
}

