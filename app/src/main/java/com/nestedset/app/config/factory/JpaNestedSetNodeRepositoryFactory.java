package com.nestedset.app.config.factory;

import com.nestedset.app.DelegatingNestedSetNodeRepository;
import com.nestedset.app.NestedSetNodeRepository;
import com.nestedset.app.config.NestedSetRepositoryConfiguration;
import com.nestedset.app.delegate.jpa.JpaNestedSetInsertingDelegate;
import com.nestedset.app.delegate.jpa.JpaNestedSetRemovingDelegate;
import com.nestedset.app.delegate.jpa.JpaNestedSetRetrievingDelegate;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeInserter;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeRemover;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeRetriever;
import com.nestedset.library.model.NestedSet;

public class JpaNestedSetNodeRepositoryFactory {
    public static <N extends NestedSet<ID>,ID> NestedSetNodeRepository<N,ID> create(NestedSetRepositoryConfiguration<N,ID> config, TreeBuilder<N,ID> treeBuilder){
        return new DelegatingNestedSetNodeRepository<>(
                new QueryBasedNestedSetNodeInserter<>(new JpaNestedSetInsertingDelegate<>(config)),
                new QueryBasedNestedSetNodeRemover<>(new JpaNestedSetRemovingDelegate<>(config)),
                new QueryBasedNestedSetNodeRetriever<>(new JpaNestedSetRetrievingDelegate<>(config)),
                treeBuilder
        );
    }
}
