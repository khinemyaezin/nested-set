package com.nestedset.app.service.infra;

import com.nestedset.app.config.NodeComponentFactory;
import com.nestedset.app.service.domain.CompositeNode;
import com.nestedset.app.service.domain.LeafNode;
import com.nestedset.library.model.NodeComponent;

public class NodeComponentFactoryImpl implements NodeComponentFactory<Entity,Long> {
    @Override
    public NodeComponent<Entity> createCompositeNodeComponent(Entity node) {
        return new CompositeNode<>(node);
    }

    @Override
    public NodeComponent<Entity> createLeafNodeComponent(Entity node) {
        return new LeafNode<>(node);
    }
}
