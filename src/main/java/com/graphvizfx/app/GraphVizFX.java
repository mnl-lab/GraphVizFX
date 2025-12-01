package com.graphvizfx.app;

import com.graphvizfx.model.GraphModel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphVizFX extends Application {

    private GraphModel graph = new GraphModel();

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px;");
        
        Label welcomeLabel = new Label("GraphVizFX - Graph Visualization Framework");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-padding: 20px;");
        root.setCenter(welcomeLabel);
        
        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("GraphVizFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
