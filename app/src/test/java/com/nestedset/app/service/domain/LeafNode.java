package com.nestedset.app.service.domain;

import com.nestedset.library.model.NodeComponent;

import java.util.Set;

public class LeafNode<T> extends NodeComponent<T> {
    private T node;
    private NodeComponent<T> parent;

    public LeafNode(T node) {
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
    public Set<NodeComponent<T>> getChildren() {
        return Set.of();
    }

    @Override
    public void addChild(NodeComponent<T> child) {

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
}
