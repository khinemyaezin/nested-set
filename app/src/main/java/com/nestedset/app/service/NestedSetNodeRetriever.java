package com.nestedset.app.service;

import com.nestedset.library.model.NestedSet;

import java.util.List;
import java.util.Optional;

public interface NestedSetNodeRetriever<N extends NestedSet<ID>,ID> {
    List<N> findImmediateChildren(N node);

    Optional<N> findParentOf(N node);

    List<N> findTreeAsList();

    List<N> getSubtreeAsList(N node);
}
