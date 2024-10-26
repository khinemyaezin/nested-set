package com.nestedset.app.config;

import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

public interface NodeComponentFactory<T extends NestedSet<ID>,ID> {
    NodeComponent<T> createCompositeNodeComponent(T node);
    NodeComponent<T> createLeafNodeComponent(T node);
}
