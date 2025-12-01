package com.graphvizfx.model;

public class GEdge {
    private GNode source;
    private GNode target;
    private int weight;
    private String id;

    public GEdge(GNode source, GNode target, int weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.id = source.getId() + "->" + target.getId();
    }

    public GNode getSource() {
        return source;
    }

    public void setSource(GNode source) {
        this.source = source;
        this.id = source.getId() + "->" + target.getId();
    }

    public GNode getTarget() {
        return target;
    }

    public void setTarget(GNode target) {
        this.target = target;
        this.id = source.getId() + "->" + target.getId();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getId() {
        return id;
    }
}

