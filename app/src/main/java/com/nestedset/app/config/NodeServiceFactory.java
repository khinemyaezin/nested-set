package com.nestedset.app.config;

import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NestedSet;

public abstract class NodeServiceFactory<N extends NestedSet<ID>,ID> {
    public abstract NestedSetNodeInserter<N,ID> getNodeInserter();
    public abstract NestedSetNodeRetriever<N,ID> getNodeRetriever();
    public abstract NestedSetNodeRemover<N,ID> getNodeRemover();
    public abstract TreeBuilder<N,ID> getTreeBuilder();
}
