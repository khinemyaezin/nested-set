package com.nestedset.app.config.factory;

import com.nestedset.app.NestedSetNodeRepository;
import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import com.nestedset.app.config.NestedSetRepositoryConfiguration;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NestedSet;

public class JpaNestedSetNodeRepositoryFactory {
    public static <N extends NestedSet<ID>, ID> NestedSetNodeRepository<N, ID> create(
            JpaNestedSetRepositoryConfiguration<N, ID> config,
            TreeBuilder<N, ID> treeBuilder
    ) {
        return NestedSetNodeRepositoryFactory.create(new JpaNodeServiceFactory<>(config, treeBuilder));
    }

    public static <N extends NestedSet<ID>, ID> NestedSetNodeRepository<N, ID> create(
            NestedSetRepositoryConfiguration<N, ID> config,
            TreeBuilder<N, ID> treeBuilder
    ) {
        JpaNestedSetRepositoryConfiguration<N, ID> jpaConfig = config;
        return create(jpaConfig, treeBuilder);
    }
}
