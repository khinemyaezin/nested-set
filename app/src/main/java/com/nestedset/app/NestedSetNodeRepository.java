package com.nestedset.app;

import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

import java.util.Optional;

public interface NestedSetNodeRepository<N extends NestedSet<ID>,ID> {
    void insertAsFirstRoot(N node);
    void insertAsLastChildOf(N node, N parent);
    void removeSubtree(N node);
    NodeComponent<N> getImmediateChildren(N node);
    Optional<N> getParent(N node);
    NodeComponent<N> getTree(N node);
}
