package com.nestedset.app.service;

import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

import java.util.Collection;
import java.util.List;

public interface TreeBuilder<T extends NestedSet<ID>,ID> {
    NodeComponent<T> buildTree(Collection<T> nodeList );
    NodeComponent<T> buildTree(List<T> nodeList );
    List<NodeComponent<T>> getLeafList(NodeComponent<T> node);
}
