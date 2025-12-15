# GraphVizFX


**GraphVizFX** is a professional-grade JavaFX application for **interactive graph construction, visualization, and algorithm comparison**. It is designed as both an educational tool and a solid software engineering project, combining algorithmic rigor with an intuitive and responsive user interface.

The application allows users to build graphs dynamically, explore how classical algorithms behave internally, and directly compare multiple algorithms under identical conditions. Rather than focusing solely on results, GraphVizFX emphasizes **process, explanation, and visual clarity**.

---

## Why GraphVizFX

GraphVizFX stands out from typical academic graph projects by focusing on:

* **Explainability**: algorithms are visualized step by step, not treated as black boxes.
* **Comparability**: multiple algorithms can be executed and compared on the same graph.
* **Engineering quality**: clean architecture, modular design, and unit-tested core logic.
* **Extensibility**: new algorithms, layouts, and features can be added with minimal friction.

The project is suitable for:

* students learning graph theory and algorithms,
* instructors demonstrating algorithm behavior,
* developers interested in JavaFX-based visualization systems.

---

## Features

### Graph Creation & Editing

* **Interactive graph editor**: click to create nodes, connect them to form edges
* **Directed and undirected graphs**
* **Weighted edges** for shortest-path and MST algorithms
* **Context menus** for editing and deletion
* **Drag-and-drop node positioning** with stable layout behavior

### Layout Algorithms

* **Circular layout** for uniform visualization
* **Grid layout** for structured graphs
* **Force-directed layout** for automatic spatial organization

Layouts can be applied at any time without altering the underlying graph structure.

### Graph Algorithms

GraphVizFX supports the visualization of major graph algorithms:

* **Breadth-First Search (BFS)**
* **Depth-First Search (DFS)**
* **Dijkstra’s shortest path algorithm**
* **A\* pathfinding algorithm**
* **Prim’s minimum spanning tree algorithm**
* **Kruskal’s minimum spanning tree algorithm**

Each algorithm execution is:

* deterministic and reproducible,
* visually highlighted (visited nodes, selected edges),
* accompanied by textual execution logs.

### Algorithm Comparison Mode

A key feature of GraphVizFX is its **dual algorithm comparison mode**:

* Two algorithms can be executed on the **same graph** simultaneously
* Differences in traversal strategy and path selection are immediately visible
* Particularly effective for comparing BFS vs DFS or Dijkstra vs A*

This feature transforms theoretical comparisons into direct visual evidence.

### Visualization & Playback

* Step-by-step algorithm animation
* Adjustable execution speed
* Color-coded visual states
* Distance and cost labels displayed directly on nodes
* Execution log panel synchronized with visual updates

### Import & Export

* **JSON import/export** for graph persistence
* **Image export (PNG)** for reports and presentations
* Experimental OpenStreetMap import (currently disabled due to data quality limitations)

![Demo Placeholder](little_demo.gif)
---

## Requirements

* **Java 21** or higher
* **JavaFX 21.0.2**
* **Maven**

---

## Building the Project

```bash
mvn clean compile
```

---

## Running the Application

Using Maven:

```bash
mvn javafx:run
```

Or by packaging manually:

```bash
mvn clean package
java -cp target/classes:target/dependency/* com.graphvizfx.app.GraphVizFX
```

---

## Project Structure

```
src/main/java/com/graphvizfx/
├── app/              # Application entry point
├── model/            # Core graph data structures
├── algorithms/       # Algorithm implementations
├── layout/           # Graph layout strategies
├── view/             # JavaFX visualization components
├── controller/       # Event handling and coordination logic
├── io/               # Import/export (JSON, images)
└── utils/            # Shared utility helpers
```

The architecture cleanly separates algorithmic logic from UI concerns, enabling unit testing and future extensions.

---

## Usage Guide

### Creating a Graph

1. Click on the canvas to create nodes
2. Select a node, then another to create an edge
3. Drag nodes to reposition them
4. Use right-click to edit or delete elements
- PS: Ensure graph consistency when deleting nodes/edges. And you can toggle between directed and undirected modes via the buttons in the navigation bar.

### Running Algorithms

1. Select an algorithm from the dropdown menu
2. Choose a starting node if required
3. Click **Run Once** to start execution
4. Control execution using playback buttons and speed slider
5. Follow progress via visual highlights and logs

### Comparing Algorithms

1. Select an algorithm for the left panel and select start node
2. Click the **Compare** button
3. Select a second algorithm for the right panel and select start node
4. Click "Next Step" to advance both algorithms in sync

### Layout Management

* Apply layouts from the layout selector
* Switch between directed and weighted modes as needed

### File Operations

* Import graphs from JSON
* Export graphs to JSON
* Export visualizations as PNG images

---

## Testing & Quality

* Core logic and algorithms are covered by **JUnit 5 unit tests**
* UI components are intentionally excluded from unit testing
* Implementation adheres to **SOLID principles** and clean architecture
* Imports are tested for robustness against malformed data

--- 





