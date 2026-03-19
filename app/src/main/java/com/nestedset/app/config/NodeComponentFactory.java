package com.nestedset.app.config;

import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

public interface NodeComponentFactory<T extends NestedSet<ID>,ID> {
    default NodeComponent<T> createCompositeNodeComponent(T node) {
        throw new UnsupportedOperationException("No composite node creator registered");
    }

    default NodeComponent<T> createLeafNodeComponent(T node) {
        throw new UnsupportedOperationException("No leaf node creator registered");
    }
}
