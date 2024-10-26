package com.nestedset.app.service;

import com.nestedset.app.config.NodeComponentFactory;
import com.nestedset.app.service.infra.Entity;
import com.nestedset.app.service.infra.NodeComponentFactoryImpl;
import com.nestedset.app.service.domain.CompositeNode;
import com.nestedset.app.service.domain.LeafNode;
import com.nestedset.library.model.NodeComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractTreeBuilderTest {
    NodeComponentFactory<Entity,Long> factory;
    TreeBuilder<Entity,Long> treeBuilder;

    @BeforeEach
    void init() {
        factory = new NodeComponentFactoryImpl();
        treeBuilder = new TreeBuilderImpl<>(factory);
    }

    @Test
    void shouldReturnCastedProperties() {
        Entity root = mock(Entity.class);
        when(root.getId()).thenReturn(1L);
        when(root.getName()).thenReturn("Im root");
        when(root.getLft()).thenReturn(1);
        when(root.getRgt()).thenReturn(2);
        when(root.getDepth()).thenReturn(0);

        var result = treeBuilder.buildTree(List.of(root));
        Assertions.assertInstanceOf(LeafNode.class, result);
        Assertions.assertEquals("Im root", result.getNode().getName());
        Assertions.assertEquals(1L, result.getNode().getId());
    }

    //@Test
    void shouldReturnTwoLeaves_whenInputParent() {
        NodeComponent<Entity> root = mock(CompositeNode.class);
        NodeComponent<Entity> electronic = mock(CompositeNode.class);
        NodeComponent<Entity> computer = mock(LeafNode.class);
        NodeComponent<Entity> mobile = mock(LeafNode.class);

        when(root.getNode()).thenAnswer(invocationOnMock -> new Entity());
        when(electronic.getNode()).thenAnswer(invocationOnMock -> new Entity());
        when(computer.getNode()).thenAnswer(invocationOnMock -> new Entity());
        when(mobile.getNode()).thenAnswer(invocationOnMock -> new Entity());

        when(root.getNode().getLft()).thenReturn(1);
        when(root.getNode().getRgt()).thenReturn(8);
        when(root.getNode().getDepth()).thenReturn(0);

        when(electronic.getNode().getLft()).thenReturn(2);
        when(electronic.getNode().getRgt()).thenReturn(7);
        when(electronic.getNode().getDepth()).thenReturn(1);

        when(computer.getNode().getLft()).thenReturn(3);
        when(computer.getNode().getRgt()).thenReturn(4);
        when(computer.getNode().getDepth()).thenReturn(2);

        when(mobile.getNode().getLft()).thenReturn(5);
        when(mobile.getNode().getRgt()).thenReturn(6);
        when(mobile.getNode().getDepth()).thenReturn(2);

        when(root.getChildren()).thenReturn(Set.of(electronic));
        when(electronic.getParent()).thenReturn(root);
        when(computer.getParent()).thenReturn(electronic);
        when(mobile.getParent()).thenReturn(electronic);
        when(electronic.getChildren()).thenReturn(Set.of(computer, mobile));

        List<NodeComponent<Entity>> result = treeBuilder.getLeafList(root);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(computer));
        Assertions.assertTrue(result.contains(mobile));

    }

    @Test
    void shouldReturnOneParentAndTwoChildren_whenInputNodeList() {
        Entity root = mock(Entity.class);
        Entity computer = mock(Entity.class);
        Entity mobile = mock(Entity.class);

        when(root.getLft()).thenReturn(1);
        when(root.getRgt()).thenReturn(6);
        when(root.getDepth()).thenReturn(0);

        when(computer.getLft()).thenReturn(2);
        when(computer.getRgt()).thenReturn(3);
        when(computer.getDepth()).thenReturn(1);

        when(mobile.getLft()).thenReturn(4);
        when(mobile.getRgt()).thenReturn(5);
        when(mobile.getDepth()).thenReturn(1);

        var result = treeBuilder.buildTree(List.of(root, computer, mobile));

        Assertions.assertEquals(result.getNode().getLft(), 1);
        Assertions.assertEquals(2, result.getChildren().size());
    }

    @Test
    void shouldReturnFirstParent_whenInputMultipleParents() {
        Entity r1 = mock(Entity.class);
        Entity r1_1 = mock(Entity.class);
        Entity r2 = mock(Entity.class);
        Entity r2_1 = mock(Entity.class);

        when(r1.getLft()).thenReturn(1);
        when(r1.getRgt()).thenReturn(4);
        when(r1.getDepth()).thenReturn(0);

        when(r1_1.getLft()).thenReturn(2);
        when(r1_1.getRgt()).thenReturn(3);
        when(r1_1.getDepth()).thenReturn(1);

        when(r2.getLft()).thenReturn(5);
        when(r2.getRgt()).thenReturn(8);
        when(r2.getDepth()).thenReturn(0);

        when(r2_1.getLft()).thenReturn(6);
        when(r2_1.getRgt()).thenReturn(7);
        when(r2_1.getDepth()).thenReturn(1);

        var result = treeBuilder.buildTree(List.of(r1, r1_1, r2, r2_1));

        Assertions.assertEquals(result.getNode().getLft(), 1);
        Assertions.assertEquals(1, result.getChildren().size());
    }
}