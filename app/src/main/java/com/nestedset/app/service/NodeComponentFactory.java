package com.nestedset.app.service;

import com.nestedset.library.model.NodeComponent;

public interface NodeComponentFactory {
    NodeComponent createCompositeNodeComponent();
    NodeComponent createLeafNodeComponent();
}
