# GraphVizFX

A professional JavaFX application for interactive graph visualization and algorithm animation. GraphVizFX provides an intuitive interface for creating, editing, and visualizing graphs while demonstrating various graph algorithms with step-by-step animations.

## Features

### Graph Creation & Editing
- **Interactive Graph Editor**: Click to add nodes, drag to create edges
- **Graph Modes**: Support for both directed and undirected graphs
- **Weighted Edges**: Optional edge weights for weighted algorithms
- **Context Menu**: Right-click nodes and edges to delete or edit properties
- **Node Dragging**: Drag nodes to reposition them on the canvas

### Layout Algorithms
- **Circular Layout**: Arrange nodes in a circular pattern
- **Grid Layout**: Organize nodes in a grid structure
- **Force-Directed Layout**: Automatic layout using force-directed algorithm

### Graph Algorithms
GraphVizFX supports six major graph algorithms with step-by-step visualization:

1. **BFS (Breadth-First Search)**: Traverse graphs level by level
2. **DFS (Depth-First Search)**: Explore graphs using depth-first traversal
3. **Dijkstra's Algorithm**: Find shortest paths in weighted graphs
4. **A\* Algorithm**: Heuristic pathfinding with visual feedback
5. **Prim's Algorithm**: Construct minimum spanning trees
6. **Kruskal's Algorithm**: Alternative MST construction method

### Visualization Features
- **Step-by-Step Animation**: Step through algorithm execution
- **Color-Coded States**: Visual representation of algorithm progress
- **Distance Labels**: Display calculated distances on nodes
- **Dual Mode Comparison**: Compare two algorithms side-by-side
- **Execution Logs**: Detailed step-by-step algorithm logs
- **Adjustable Animation Speed**: Control playback speed with slider

### Import & Export
- **JSON Import/Export**: Save and load graph structures
- **OSM Import**: Import graph data from OpenStreetMap files
- **Image Export**: Export graph visualizations as PNG images

## Requirements

- Java 17 or higher
- JavaFX 17.0.6 or higher
- Maven 3.6+ (for building)

## Building

```bash
mvn clean compile
```

## Running

```bash
mvn javafx:run
```

Or compile and run manually:

```bash
mvn clean package
java -cp target/classes:target/dependency/* com.graphvizfx.app.GraphVizFX
```

## Project Structure

```
src/main/java/com/graphvizfx/
├── app/              # Main application entry point
├── model/            # Graph data models (GNode, GEdge, GraphModel, VisualState)
├── view/             # UI components (GraphCanvas)
├── controller/       # Business logic and event handling
├── algorithms/       # Graph algorithm implementations
├── layout/           # Graph layout algorithms
├── io/               # File import/export handlers
└── utils/            # Utility functions
```

## Usage

### Creating a Graph
1. Click anywhere on the canvas to add a node
2. Click a node, then click another node to create an edge
3. Drag nodes to reposition them
4. Right-click nodes or edges to delete them

### Running Algorithms
1. Select an algorithm from the dropdown menu
2. Click "Run" to execute the algorithm
3. Use playback controls to step through the algorithm
4. View detailed logs in the execution log panel

### Comparing Algorithms
1. Select the first algorithm
2. Click "Dual Mode"
3. Choose a second algorithm to compare
4. Both algorithms run side-by-side for comparison

### Layout Management
- Use the Layout dropdown in the toolbar to apply different layouts
- Toggle "Directed" and "Weighted" modes as needed

### File Operations
- **Import JSON**: Load a previously saved graph
- **Export JSON**: Save the current graph structure
- **Export Image**: Save the current visualization as PNG

## Architecture

GraphVizFX follows a clean architecture pattern:

- **Model**: Pure data classes with no UI dependencies
- **View**: UI components that render the model
- **Controller**: Mediates between view and model, handles business logic
- **Algorithms**: Strategy pattern for extensible algorithm system
- **Layout**: Interface-based layout system for easy extension
- **I/O**: Separate package for file operations
