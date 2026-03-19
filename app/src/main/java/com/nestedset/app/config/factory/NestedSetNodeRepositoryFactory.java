package com.nestedset.app.config.factory;

import com.nestedset.app.DelegatingNestedSetNodeRepository;
import com.nestedset.app.NestedSetNodeRepository;
import com.nestedset.app.config.NodeServiceFactory;
import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NestedSet;

public final class NestedSetNodeRepositoryFactory {
    private NestedSetNodeRepositoryFactory() {
    }

    public static <N extends NestedSet<ID>, ID> NestedSetNodeRepository<N, ID> create(
            NodeServiceFactory<N, ID> nodeServiceFactory
    ) {
        return create(
                nodeServiceFactory.getNodeInserter(),
                nodeServiceFactory.getNodeRemover(),
                nodeServiceFactory.getNodeRetriever(),
                nodeServiceFactory.getTreeBuilder()
        );
    }

    public static <N extends NestedSet<ID>, ID> NestedSetNodeRepository<N, ID> create(
            NestedSetNodeInserter<N, ID> inserter,
            NestedSetNodeRemover<N, ID> remover,
            NestedSetNodeRetriever<N, ID> retriever,
            TreeBuilder<N, ID> treeBuilder
    ) {
        return new DelegatingNestedSetNodeRepository<>(inserter, remover, retriever, treeBuilder);
    }
}
