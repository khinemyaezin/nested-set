package com.nestedset.app.service;

import com.nestedset.library.model.NodeComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TreeBuilder {
    Optional<NodeComponent> buildTree(Collection<? extends NodeComponent> nodeList );
    Optional<NodeComponent> buildTree(List<NodeComponent> nodeList );
    List<NodeComponent> getLeafList(NodeComponent node);
}
