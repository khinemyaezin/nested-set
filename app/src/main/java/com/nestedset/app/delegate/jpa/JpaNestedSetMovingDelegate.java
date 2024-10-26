package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.NestedSetRepositoryConfiguration;
import com.nestedset.app.delegate.NestedSetMovingDelegate;
import com.nestedset.library.model.NestedSet;

public class JpaNestedSetMovingDelegate<N extends NestedSet<ID>,ID> extends JpaNestedSetDelegate<N,ID> implements NestedSetMovingDelegate<N,ID> {

    protected JpaNestedSetMovingDelegate(NestedSetRepositoryConfiguration<N,ID> nestedSetRepositoryConfiguration) {
        super(nestedSetRepositoryConfiguration);
    }


}
