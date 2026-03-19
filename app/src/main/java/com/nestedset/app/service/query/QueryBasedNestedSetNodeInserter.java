package com.nestedset.app.service.query;

import com.nestedset.app.delegate.NestedSetInsertingDelegate;
import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.library.model.NestedSet;

public class QueryBasedNestedSetNodeInserter<N extends NestedSet<ID>,ID> implements NestedSetNodeInserter<N,ID> {

    private final NestedSetInsertingDelegate<N,ID> queryDelegate;

    public QueryBasedNestedSetNodeInserter(NestedSetInsertingDelegate<N,ID> queryDelegate) {
        this.queryDelegate = queryDelegate;
    }

    @Override
    public N createAsRoot(N entity) {
        Integer right = queryDelegate.getMaxRight();
        ensureRootExists(right);
        right = 1;

        entity.setLft(right);
        entity.setRgt(right + 1);
        entity.setDepth(0);

        queryDelegate.insert(entity);
        return entity;
    }

    private void ensureRootExists(Integer right) {
        if (right != null && right > 0) {
            throw new IllegalStateException("Root node already exists. Only one root node is allowed.");
        }
    }

    @Override
    public N createAsLastOf(N entity, N rootNode) {
        Integer right = rootNode.getRgt();

        queryDelegate.incrementLeftBoundaryAfter(right);
        queryDelegate.incrementRightBoundaryAfter(right);

        entity.setLft(right);
        entity.setRgt(right + 1);
        entity.setDepth(rootNode.getDepth() + 1);

        queryDelegate.insert(entity);
        return entity;
    }
}
