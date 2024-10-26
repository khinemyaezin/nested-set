package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.NestedSetRepositoryConfiguration;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeField;
import jakarta.persistence.EntityManager;

public abstract class JpaNestedSetDelegate<N extends NestedSet<ID>,ID> {
    protected final EntityManager entityManager;
    protected final NodeField configs;
    protected final Class<N> entityClassType;

    protected JpaNestedSetDelegate(NestedSetRepositoryConfiguration<N,ID> nestedSetRepositoryConfiguration) {
        entityManager =  nestedSetRepositoryConfiguration.getEntityManager();
        configs = nestedSetRepositoryConfiguration.getConfigs();
        entityClassType = nestedSetRepositoryConfiguration.getEntityClassType();
    }
}
