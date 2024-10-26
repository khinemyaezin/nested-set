package com.nestedset.app.delegate;

import com.nestedset.library.model.NestedSet;

public interface NestedSetInsertingDelegate<N extends NestedSet<ID>,ID> {
    void insert(N node);

    Integer getMaxRight();

    void incrementLeftBoundaryAfter(Integer right);

    void incrementRightBoundaryAfter(Integer right);

}
