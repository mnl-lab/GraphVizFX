package com.graphvizfx.app;

import com.graphvizfx.controller.GraphController;
import com.graphvizfx.view.GraphCanvas;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphVizFX extends Application {

    private GraphController controller;
    private GraphCanvas mainCanvas;

    @Override
    public void start(Stage primaryStage) {
        controller = new GraphController();
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px;");
        
        mainCanvas = new GraphCanvas(controller, true);
        root.setCenter(mainCanvas);
        
        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("GraphVizFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
