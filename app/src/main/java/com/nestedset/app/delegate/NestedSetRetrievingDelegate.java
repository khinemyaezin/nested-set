package com.nestedset.app.delegate;

import com.nestedset.library.model.NestedSet;

import java.util.List;
import java.util.Optional;

public interface NestedSetRetrievingDelegate<N extends NestedSet<ID>,ID> {
    List<N> getImmediateChildren(N node);

    Optional<N> getParentOf(N node);

    List<N> getTreeAsList();

    List<N> getSubtreeAsList(N node);
}
