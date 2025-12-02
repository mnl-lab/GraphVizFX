package com.graphvizfx.view;

import com.graphvizfx.controller.GraphController;
import com.graphvizfx.model.GEdge;
import com.graphvizfx.model.GNode;
import com.graphvizfx.model.GraphModel;
import com.graphvizfx.model.VisualState;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GraphCanvas extends Pane {
    private Canvas canvas;
    private GraphController controller;
    private boolean interactive;
    private VisualState currentState;

    private GNode selectedNode = null;
    private GNode edgeStart = null;
    private boolean dragging = false;

    public GraphCanvas(GraphController controller, boolean interactive) {
        this.controller = controller;
        this.interactive = interactive;
        this.canvas = new Canvas();
        getChildren().add(canvas);

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener(e -> draw());
        heightProperty().addListener(e -> draw());

        if (interactive) {
            setupEvents();
        }
    }

    private void setupEvents() {
        canvas.setOnMousePressed(e -> {
            GraphModel graph = controller.getGraph();
            GNode hit = hitTest(e.getX(), e.getY(), graph);

            if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                // Right-clicking a node deletes it immediately (no context menu)
                if (hit != null) {
                    controller.deleteNode(hit);
                    draw();
                    return;
                }

                // If not clicking a node, preserve edge context menu behavior
                GEdge edgeHit = hitTestEdge(e.getX(), e.getY(), graph);
                if (edgeHit != null) {
                    javafx.scene.control.ContextMenu cm = new javafx.scene.control.ContextMenu();
                    javafx.scene.control.MenuItem del = new javafx.scene.control.MenuItem("Delete Edge");
                    del.setOnAction(ev -> {
                        controller.deleteEdge(edgeHit);
                        draw();
                    });
                    javafx.scene.control.MenuItem w = new javafx.scene.control.MenuItem("Edit Weight");
                    w.setOnAction(ev -> {
                        javafx.scene.control.TextInputDialog td = new javafx.scene.control.TextInputDialog("" + edgeHit.getWeight());
                        td.showAndWait().ifPresent(s -> {
                            try {
                                edgeHit.setWeight(Integer.parseInt(s));
                                draw();
                            } catch (Exception x) {
                            }
                        });
                    });
                    cm.getItems().addAll(del, w);
                    cm.show(canvas, e.getScreenX(), e.getScreenY());
                }
                return;
            }

            if (hit != null) {
                if (edgeStart == null) {
                    selectedNode = hit;
                    edgeStart = hit;
                    dragging = true;
                } else {
                    if (edgeStart != hit) {
                        if (!graph.hasEdge(edgeStart, hit)) {
                            int w = graph.isWeighted() ? (int) dist(edgeStart, hit) : 1;
                            controller.addEdge(edgeStart, hit, w);
                        }
                    }
                    edgeStart = null;
                    selectedNode = null;
                }
            } else {
                if (edgeStart != null) {
                    edgeStart = null;
                } else {
                    controller.addNode("N" + controller.getNextNodeId(), e.getX(), e.getY());
                }
            }
            draw();
        });

        canvas.setOnMouseDragged(e -> {
            if (dragging && selectedNode != null) {
                edgeStart = null;
                selectedNode.setX(Math.max(20, Math.min(getWidth() - 20, e.getX())));
                selectedNode.setY(Math.max(20, Math.min(getHeight() - 20, e.getY())));
                draw();
            }
        });

        canvas.setOnMouseReleased(e -> dragging = false);
    }

    private GNode hitTest(double x, double y, GraphModel graph) {
        for (GNode n : graph.getNodes()) {
            if (Math.hypot(n.getX() - x, n.getY() - y) <= n.getRadius()) {
                return n;
            }
        }
        return null;
    }

    private GEdge hitTestEdge(double x, double y, GraphModel graph) {
        for (GEdge e : graph.getEdges()) {
            double d = ptSegDist(e.getSource().getX(), e.getSource().getY(),
                    e.getTarget().getX(), e.getTarget().getY(), x, y);
            if (d < 5) {
                return e;
            }
        }
        return null;
    }

    public void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = getWidth();
        double h = getHeight();

        gc.setFill(Color.web("#fcfcfc"));
        gc.fillRect(0, 0, w, h);

        gc.setStroke(Color.web("#eeeeee"));
        gc.setLineWidth(1);
        for (double i = 0; i < w; i += 50) {
            gc.strokeLine(i, 0, i, h);
        }
        for (double i = 0; i < h; i += 50) {
            gc.strokeLine(0, i, w, i);
        }

        GraphModel graph = controller.getGraph();
        for (GEdge e : graph.getEdges()) {
            drawEdge(gc, e, graph);
        }
        for (GNode n : graph.getNodes()) {
            drawNode(gc, n);
        }
    }

    private void drawEdge(GraphicsContext gc, GEdge e, GraphModel graph) {
        Color c = Color.GRAY;
        double width = 1.5;

        if (currentState != null && currentState.getEdgeColors().containsKey(e.getId())) {
            c = currentState.getEdgeColors().get(e.getId());
            width = 3.0;
        }

        gc.setStroke(c);
        gc.setLineWidth(width);

        double sx = e.getSource().getX(), sy = e.getSource().getY();
        double tx = e.getTarget().getX(), ty = e.getTarget().getY();
        boolean reverseExists = graph.hasEdge(e.getTarget(), e.getSource());

        if (graph.isDirected() && reverseExists) {
            double mx = (sx + tx) / 2;
            double my = (sy + ty) / 2;
            double dx = tx - sx, dy = ty - sy;
            double dist = Math.hypot(dx, dy);
            double nx = -dy / dist * 30;
            double ny = dx / dist * 30;

            gc.beginPath();
            gc.moveTo(sx, sy);
            gc.quadraticCurveTo(mx + nx, my + ny, tx, ty);
            gc.stroke();
            drawArrow(gc, mx + nx, my + ny, tx, ty, c);
            if (graph.isWeighted()) {
                drawWeight(gc, mx + nx, my + ny, e.getWeight());
            }
        } else {
            gc.strokeLine(sx, sy, tx, ty);
            if (graph.isDirected()) {
                drawArrow(gc, sx, sy, tx, ty, c);
            }
            if (graph.isWeighted()) {
                drawWeight(gc, (sx + tx) / 2, (sy + ty) / 2, e.getWeight());
            }
        }
    }

    private void drawArrow(GraphicsContext gc, double sx, double sy, double tx, double ty, Color c) {
        double angle = Math.atan2(ty - sy, tx - sx);
        double r = 20;
        double targetX = tx - r * Math.cos(angle);
        double targetY = ty - r * Math.sin(angle);

        double arrowSize = 10;
        double x1 = targetX - arrowSize * Math.cos(angle - Math.PI / 6);
        double y1 = targetY - arrowSize * Math.sin(angle - Math.PI / 6);
        double x2 = targetX - arrowSize * Math.cos(angle + Math.PI / 6);
        double y2 = targetY - arrowSize * Math.sin(angle + Math.PI / 6);

        gc.setFill(c);
        gc.fillPolygon(new double[]{targetX, x1, x2}, new double[]{targetY, y1, y2}, 3);
    }

    private void drawWeight(GraphicsContext gc, double x, double y, int w) {
        gc.setFill(Color.WHITE);
        gc.fillRect(x - 10, y - 8, 20, 16);
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeRect(x - 10, y - 8, 20, 16);
        gc.setFill(Color.BLUE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(10));
        gc.fillText("" + w, x, y + 4);
    }

    private void drawNode(GraphicsContext gc, GNode n) {
        Color fill = Color.WHITE;
        if (currentState != null && currentState.getNodeColors().containsKey(n.getId())) {
            fill = currentState.getNodeColors().get(n.getId());
        } else if (n == edgeStart) {
            fill = Color.LIGHTGREEN;
        }

        gc.setFill(Color.rgb(0, 0, 0, 0.1));
        gc.fillOval(n.getX() - n.getRadius() + 3, n.getY() - n.getRadius() + 3, n.getRadius() * 2, n.getRadius() * 2);

        gc.setFill(fill);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);
        gc.fillOval(n.getX() - n.getRadius(), n.getY() - n.getRadius(), n.getRadius() * 2, n.getRadius() * 2);
        gc.strokeOval(n.getX() - n.getRadius(), n.getY() - n.getRadius(), n.getRadius() * 2, n.getRadius() * 2);

        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 12));
        gc.fillText(n.getId(), n.getX(), n.getY() + 4);

        if (currentState != null && currentState.getDistances().containsKey(n.getId())) {
            double d = currentState.getDistances().get(n.getId());
            String t = (d == Double.MAX_VALUE) ? "∞" : String.format("%.0f", d);
            gc.setFill(Color.RED);
            gc.setFont(Font.font(11));
            gc.fillText(t, n.getX(), n.getY() - 24);
        }
    }

    private double ptSegDist(double x1, double y1, double x2, double y2, double px, double py) {
        double dx = x2 - x1, dy = y2 - y1;
        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        return Math.hypot(px - (x1 + t * dx), py - (y1 + t * dy));
    }

    private double dist(GNode a, GNode b) {
        return Math.hypot(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public void setCurrentState(VisualState state) {
        this.currentState = state;
    }

    public VisualState getCurrentState() {
        return currentState;
    }
}

