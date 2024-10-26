package com.nestedset.library.model;

import java.util.Set;

public abstract class NodeComponent<T> {
    public NodeComponent(T node) {
        setNode(node);
    }

    public abstract T getNode();

    public abstract void setNode(T node);

    public abstract Set<NodeComponent<T>> getChildren();

    public abstract void addChild(NodeComponent<T> child);

    public abstract NodeComponent<T> getParent();

    public abstract void setParent(NodeComponent<T> parent);

    public abstract void print(String i);
}
