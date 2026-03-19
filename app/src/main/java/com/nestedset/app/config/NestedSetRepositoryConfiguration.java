package com.nestedset.app.config;

import com.nestedset.library.model.NestedSet;
import org.springframework.data.jpa.repository.JpaContext;

/**
 * @deprecated Use {@link JpaNestedSetRepositoryConfiguration} directly.
 */
@Deprecated
public class NestedSetRepositoryConfiguration<N extends NestedSet<ID>, ID>
        extends JpaNestedSetRepositoryConfiguration<N, ID> {

    public NestedSetRepositoryConfiguration(JpaContext context, Class<N> entityClassType) {
        super(context, entityClassType);
    }
}
