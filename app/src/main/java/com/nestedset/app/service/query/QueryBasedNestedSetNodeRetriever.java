package com.nestedset.app.service.query;

import com.nestedset.app.delegate.NestedSetRetrievingDelegate;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.library.model.NestedSet;

import java.util.List;
import java.util.Optional;

public class QueryBasedNestedSetNodeRetriever<N extends NestedSet<ID>, ID> implements NestedSetNodeRetriever<N,ID> {
    protected final NestedSetRetrievingDelegate<N,ID> queryDelegate;

    public QueryBasedNestedSetNodeRetriever(NestedSetRetrievingDelegate<N, ID> queryDelegate) {
        this.queryDelegate = queryDelegate;
    }

    @Override
    public List<N> findImmediateChildren(N node) {
        return this.queryDelegate.getImmediateChildren(node);
    }

    @Override
    public Optional<N> findParentOf(N node) {
        return this.queryDelegate.getParentOf(node);
    }

    @Override
    public List<N> findTreeAsList() {
        return this.queryDelegate.getTreeAsList();
    }
}
