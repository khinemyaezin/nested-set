package com.nestedset.app.delegate;

import com.nestedset.library.model.NestedSet;

public interface NestedSetRemovingDelegate<N extends NestedSet<ID>,ID> {
    void removeNodesInRange(Integer left, Integer right);

    void decrementLeftBoundaryAfter(Integer right, Integer width);

    void decrementRightBoundaryAfter(Integer right, Integer width);
}
