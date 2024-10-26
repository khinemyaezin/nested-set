package com.nestedset.app.service.domain;

import com.nestedset.library.model.NodeComponent;

import java.util.HashSet;
import java.util.Set;

public class CompositeNode<T> extends NodeComponent<T> {
    private T node;
    private NodeComponent<T> parent;
    private final Set<NodeComponent<T>> children = new HashSet<>();

    public CompositeNode(T node) {
        super(node);
    }

    @Override
    public T getNode() {
        return node;
    }

    @Override
    public void setNode(T node) {
        this.node = node;
    }

    @Override
    public NodeComponent<T> getParent() {
        return parent;
    }

    @Override
    public void setParent(NodeComponent<T> parent) {
        this.parent = parent;
    }

    @Override
    public void print(String i) {

    }

    @Override
    public Set<NodeComponent<T>> getChildren() {
        return children;
    }

    @Override
    public void addChild(NodeComponent<T> child) {
        this.children.add(child);
    }
}
