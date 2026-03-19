package com.nestedset.app.config.factory;

import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import com.nestedset.app.config.NodeServiceFactory;
import com.nestedset.app.delegate.jpa.JpaNestedSetInsertingDelegate;
import com.nestedset.app.delegate.jpa.JpaNestedSetRemovingDelegate;
import com.nestedset.app.delegate.jpa.JpaNestedSetRetrievingDelegate;
import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeInserter;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeRemover;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeRetriever;
import com.nestedset.library.model.NestedSet;

public class JpaNodeServiceFactory<N extends NestedSet<ID>, ID> extends NodeServiceFactory<N, ID> {
    private final NestedSetNodeInserter<N, ID> nodeInserter;
    private final NestedSetNodeRetriever<N, ID> nodeRetriever;
    private final NestedSetNodeRemover<N, ID> nodeRemover;
    private final TreeBuilder<N, ID> treeBuilder;

    public JpaNodeServiceFactory(JpaNestedSetRepositoryConfiguration<N, ID> config, TreeBuilder<N, ID> treeBuilder) {
        this.nodeInserter = new QueryBasedNestedSetNodeInserter<>(new JpaNestedSetInsertingDelegate<>(config));
        this.nodeRetriever = new QueryBasedNestedSetNodeRetriever<>(new JpaNestedSetRetrievingDelegate<>(config));
        this.nodeRemover = new QueryBasedNestedSetNodeRemover<>(new JpaNestedSetRemovingDelegate<>(config));
        this.treeBuilder = treeBuilder;
    }

    @Override
    public NestedSetNodeInserter<N, ID> getNodeInserter() {
        return nodeInserter;
    }

    @Override
    public NestedSetNodeRetriever<N, ID> getNodeRetriever() {
        return nodeRetriever;
    }

    @Override
    public NestedSetNodeRemover<N, ID> getNodeRemover() {
        return nodeRemover;
    }

    @Override
    public TreeBuilder<N, ID> getTreeBuilder() {
        return treeBuilder;
    }
}
