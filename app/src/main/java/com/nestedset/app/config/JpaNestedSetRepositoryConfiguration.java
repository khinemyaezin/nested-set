package com.nestedset.app.config;

import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeField;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaContext;

public class JpaNestedSetRepositoryConfiguration<N extends NestedSet<ID>, ID> {
    private final EntityManager entityManager;
    private final NestedSetMetadataConfiguration<N, ID> metadataConfiguration;

    public JpaNestedSetRepositoryConfiguration(JpaContext context, Class<N> entityClassType) {
        this(
                context.getEntityManagerByManagedType(entityClassType),
                new NestedSetMetadataConfiguration<>(entityClassType)
        );
    }

    public JpaNestedSetRepositoryConfiguration(
            EntityManager entityManager,
            NestedSetMetadataConfiguration<N, ID> metadataConfiguration
    ) {
        this.entityManager = entityManager;
        this.metadataConfiguration = metadataConfiguration;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public NodeField getConfigs() {
        return metadataConfiguration.getConfigs();
    }

    public Class<N> getEntityClassType() {
        return metadataConfiguration.getEntityClassType();
    }

    public NestedSetMetadataConfiguration<N, ID> getMetadataConfiguration() {
        return metadataConfiguration;
    }
}
