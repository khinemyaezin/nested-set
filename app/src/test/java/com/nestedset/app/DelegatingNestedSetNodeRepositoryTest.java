package com.nestedset.app;

import com.nestedset.app.delegate.jpa.TestNode;
import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NodeComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelegatingNestedSetNodeRepositoryTest {

    @Mock
    private NestedSetNodeInserter<TestNode, Long> inserter;

    @Mock
    private NestedSetNodeRemover<TestNode, Long> remover;

    @Mock
    private NestedSetNodeRetriever<TestNode, Long> retriever;

    @Mock
    private TreeBuilder<TestNode, Long> treeBuilder;

    @Mock
    private NodeComponent<TestNode> mockNodeComponent;

    private DelegatingNestedSetNodeRepository<TestNode, Long> repository;

    @BeforeEach
    void setUp() {
        repository = new DelegatingNestedSetNodeRepository<>(inserter, remover, retriever, treeBuilder);
    }

    @Test
    void getImmediateChildren_shouldHandleMutableList() {
        TestNode parent = new TestNode(1L, "parent", 1, 6, 0);
        TestNode child1 = new TestNode(2L, "child1", 2, 3, 1);
        TestNode child2 = new TestNode(3L, "child2", 4, 5, 1);

        List<TestNode> mutableChildren = new ArrayList<>();
        mutableChildren.add(child1);
        mutableChildren.add(child2);

        when(retriever.findImmediateChildren(parent)).thenReturn(mutableChildren);
        when(treeBuilder.buildTree(anyList())).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getImmediateChildren(parent);

        assertNotNull(result);
        verify(retriever).findImmediateChildren(parent);
        verify(treeBuilder).buildTree(argThat(list ->
            list.size() == 3 &&
            list.getFirst().equals(parent) &&
            list.get(1).equals(child1) &&
            list.get(2).equals(child2)
        ));

        assertEquals(2, mutableChildren.size());
    }

    @Test
    void getImmediateChildren_shouldHandleImmutableList() {
        TestNode parent = new TestNode(1L, "parent", 1, 6, 0);
        TestNode child1 = new TestNode(2L, "child1", 2, 3, 1);
        TestNode child2 = new TestNode(3L, "child2", 4, 5, 1);

        List<TestNode> immutableChildren = List.of(child1, child2);

        when(retriever.findImmediateChildren(parent)).thenReturn(immutableChildren);
        when(treeBuilder.buildTree(anyList())).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getImmediateChildren(parent);

        assertNotNull(result);
        verify(retriever).findImmediateChildren(parent);
        verify(treeBuilder).buildTree(argThat(list ->
            list.size() == 3 &&
            list.getFirst().equals(parent) &&
            list.get(1).equals(child1) &&
            list.get(2).equals(child2)
        ));
    }

    @Test
    void getImmediateChildren_shouldHandleEmptyChildrenList() {
        TestNode parent = new TestNode(1L, "parent", 1, 2, 0);
        List<TestNode> emptyChildren = Collections.emptyList();

        when(retriever.findImmediateChildren(parent)).thenReturn(emptyChildren);
        when(treeBuilder.buildTree(anyList())).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getImmediateChildren(parent);

        assertNotNull(result);
        verify(retriever).findImmediateChildren(parent);
        verify(treeBuilder).buildTree(argThat(list ->
            list.size() == 1 &&
            list.getFirst().equals(parent)
        ));
    }

    @Test
    void getImmediateChildren_shouldPreserveChildrenOrder() {
        TestNode parent = new TestNode(1L, "parent", 1, 10, 0);
        TestNode child1 = new TestNode(2L, "child1", 2, 3, 1);
        TestNode child2 = new TestNode(3L, "child2", 4, 5, 1);
        TestNode child3 = new TestNode(4L, "child3", 6, 7, 1);
        TestNode child4 = new TestNode(5L, "child4", 8, 9, 1);

        List<TestNode> children = List.of(child1, child2, child3, child4);

        when(retriever.findImmediateChildren(parent)).thenReturn(children);
        when(treeBuilder.buildTree(anyList())).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getImmediateChildren(parent);

        assertNotNull(result);
        verify(treeBuilder).buildTree(argThat(list ->
            list.size() == 5 &&
            list.getFirst().equals(parent) &&
            list.get(1).equals(child1) &&
            list.get(2).equals(child2) &&
            list.get(3).equals(child3) &&
            list.get(4).equals(child4)
        ));
    }

    @Test
    void insertAsFirstRoot_shouldDelegateToInserter() {
        TestNode node = new TestNode(1L, "root", 1, 2, 0);

        repository.insertAsFirstRoot(node);

        verify(inserter).createAsRoot(node);
    }

    @Test
    void insertAsLastChildOf_shouldDelegateToInserter() {
        TestNode parent = new TestNode(1L, "parent", 1, 4, 0);
        TestNode child = new TestNode(2L, "child", 2, 3, 1);

        repository.insertAsLastChildOf(child, parent);

        verify(inserter).createAsLastOf(child, parent);
    }

    @Test
    void removeSubtree_shouldDelegateToRemover() {
        TestNode node = new TestNode(1L, "node", 1, 2, 0);

        repository.removeSubtree(node);

        verify(remover).deleteNode(node);
    }

    @Test
    void getParent_shouldDelegateToRetriever() {
        TestNode node = new TestNode(2L, "child", 2, 3, 1);
        TestNode parent = new TestNode(1L, "parent", 1, 4, 0);

        when(retriever.findParentOf(node)).thenReturn(Optional.of(parent));

        Optional<TestNode> result = repository.getParent(node);

        assertTrue(result.isPresent());
        assertEquals(parent, result.get());
        verify(retriever).findParentOf(node);
    }

    @Test
    void getTree_shouldDelegateToRetrieverAndTreeBuilder() {
        TestNode node = new TestNode(1L, "root", 1, 10, 0);
        List<TestNode> treeList = List.of(node);

        when(retriever.findTreeAsList()).thenReturn(treeList);
        when(treeBuilder.buildTree(treeList)).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getTree(node);

        assertNotNull(result);
        verify(retriever).findTreeAsList();
        verify(treeBuilder).buildTree(treeList);
    }

    @Test
    void getSubtreeOfA_shouldDelegateToRetrieverAndTreeBuilder() {
        TestNode b    = new TestNode(4L, "B",    6,  9, 1);
        TestNode d    = new TestNode(5L, "D",    7,  8, 2);
        List<TestNode> treeList = List.of(b,d);

        when(retriever.getSubtreeAsList(b)).thenReturn(treeList);
        when(treeBuilder.buildTree(treeList)).thenReturn(mockNodeComponent);

        NodeComponent<TestNode> result = repository.getSubtreeOf(b);

        assertNotNull(result);
        verify(retriever).getSubtreeAsList(b);
        verify(treeBuilder).buildTree(treeList);
    }
}
