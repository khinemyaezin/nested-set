package com.nestedset.app.service.query;

import com.nestedset.app.delegate.NestedSetRemovingDelegate;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.library.model.NestedSet;


public class QueryBasedNestedSetNodeRemover<N extends NestedSet<ID>,ID> implements NestedSetNodeRemover<N,ID> {

    private final NestedSetRemovingDelegate<N,ID> queryDelegate;

    public QueryBasedNestedSetNodeRemover(NestedSetRemovingDelegate<N,ID> queryDelegate) {
        this.queryDelegate = queryDelegate;
    }


    @Override
    public void deleteNode(N node) {
        Integer left = node.getLft();
        Integer right = node.getRgt();
        Integer width = right - left + 1;

        this.queryDelegate.removeNodesInRange(left, right);
        this.queryDelegate.decrementRightBoundaryAfter(right, width);
        this.queryDelegate.decrementLeftBoundaryAfter(right, width);
    }
}
