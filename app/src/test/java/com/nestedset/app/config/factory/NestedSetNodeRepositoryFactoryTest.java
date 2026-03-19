package com.nestedset.app.config.factory;

import com.nestedset.app.NestedSetNodeRepository;
import com.nestedset.app.config.NodeServiceFactory;
import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NestedSetNodeRepositoryFactoryTest {

    @Test
    void create_shouldUseCustomServiceFactoryWithoutChangingCoreFactory() {
        TestNestedNode root = new TestNestedNode(1L, 1, 4, 0);
        TestNestedNode child = new TestNestedNode(2L, 2, 3, 1);

        RecordingInserter inserter = new RecordingInserter();
        RecordingRemover remover = new RecordingRemover();
        RecordingRetriever retriever = new RecordingRetriever(root, child);
        RecordingTreeBuilder treeBuilder = new RecordingTreeBuilder();

        NodeServiceFactory<TestNestedNode, Long> customFactory = new NodeServiceFactory<>() {
            @Override
            public NestedSetNodeInserter<TestNestedNode, Long> getNodeInserter() {
                return inserter;
            }

            @Override
            public NestedSetNodeRetriever<TestNestedNode, Long> getNodeRetriever() {
                return retriever;
            }

            @Override
            public NestedSetNodeRemover<TestNestedNode, Long> getNodeRemover() {
                return remover;
            }

            @Override
            public TreeBuilder<TestNestedNode, Long> getTreeBuilder() {
                return treeBuilder;
            }
        };

        NestedSetNodeRepository<TestNestedNode, Long> repository = NestedSetNodeRepositoryFactory.create(customFactory);

        repository.insertAsFirstRoot(root);
        repository.insertAsLastChildOf(child, root);
        repository.removeSubtree(child);
        NodeComponent<TestNestedNode> immediateChildren = repository.getImmediateChildren(root);
        Optional<TestNestedNode> parent = repository.getParent(child);
        List<TestNestedNode> leafNodes = repository.getLeafNodes(root);
        NodeComponent<TestNestedNode> tree = repository.getTree(root);
        NodeComponent<TestNestedNode> subtree = repository.getSubtreeOf(root);

        assertSame(root, inserter.lastRootInserted);
        assertSame(child, inserter.lastChildInserted);
        assertSame(root, inserter.lastParentForChildInsert);
        assertSame(child, remover.lastRemovedNode);
        assertTrue(parent.isPresent());
        assertSame(root, parent.get());
        assertEquals(List.of(child), leafNodes);
        assertSame(treeBuilder.builtNodeComponent, immediateChildren);
        assertSame(treeBuilder.builtNodeComponent, tree);
        assertSame(treeBuilder.builtNodeComponent, subtree);
        assertEquals(List.of(root, child), treeBuilder.lastBuiltNodes);
    }

    private static class RecordingInserter implements NestedSetNodeInserter<TestNestedNode, Long> {
        private TestNestedNode lastRootInserted;
        private TestNestedNode lastChildInserted;
        private TestNestedNode lastParentForChildInsert;

        @Override
        public TestNestedNode createAsRoot(TestNestedNode entity) {
            this.lastRootInserted = entity;
            return entity;
        }

        @Override
        public TestNestedNode createAsLastOf(TestNestedNode entity, TestNestedNode rootNode) {
            this.lastChildInserted = entity;
            this.lastParentForChildInsert = rootNode;
            return entity;
        }
    }

    private static class RecordingRemover implements NestedSetNodeRemover<TestNestedNode, Long> {
        private TestNestedNode lastRemovedNode;

        @Override
        public void deleteNode(TestNestedNode node) {
            this.lastRemovedNode = node;
        }
    }

    private static class RecordingRetriever implements NestedSetNodeRetriever<TestNestedNode, Long> {
        private final TestNestedNode root;
        private final TestNestedNode child;

        private RecordingRetriever(TestNestedNode root, TestNestedNode child) {
            this.root = root;
            this.child = child;
        }

        @Override
        public List<TestNestedNode> findImmediateChildren(TestNestedNode node) {
            return List.of(child);
        }

        @Override
        public Optional<TestNestedNode> findParentOf(TestNestedNode node) {
            return Optional.of(root);
        }

        @Override
        public List<TestNestedNode> getLeafNodes(TestNestedNode node) {
            return List.of(child);
        }

        @Override
        public List<TestNestedNode> findTreeAsList() {
            return List.of(root, child);
        }

        @Override
        public List<TestNestedNode> getSubtreeAsList(TestNestedNode node) {
            return List.of(root, child);
        }
    }

    private static class RecordingTreeBuilder implements TreeBuilder<TestNestedNode, Long> {
        private final NodeComponent<TestNestedNode> builtNodeComponent =
                new TestNodeComponent(new TestNestedNode(999L, 1, 2, 0));
        private List<TestNestedNode> lastBuiltNodes = new ArrayList<>();

        @Override
        public NodeComponent<TestNestedNode> buildTree(Collection<TestNestedNode> nodeList) {
            this.lastBuiltNodes = new ArrayList<>(nodeList);
            return builtNodeComponent;
        }

        @Override
        public NodeComponent<TestNestedNode> buildTree(List<TestNestedNode> nodeList) {
            this.lastBuiltNodes = new ArrayList<>(nodeList);
            return builtNodeComponent;
        }

        @Override
        public List<NodeComponent<TestNestedNode>> getLeafList(NodeComponent<TestNestedNode> node) {
            return List.of();
        }
    }

    private static class TestNestedNode implements NestedSet<Long> {
        private Long id;
        private Integer lft;
        private Integer rgt;
        private Integer depth;

        private TestNestedNode(Long id, Integer lft, Integer rgt, Integer depth) {
            this.id = id;
            this.lft = lft;
            this.rgt = rgt;
            this.depth = depth;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public Integer getLft() {
            return lft;
        }

        @Override
        public void setLft(Integer lft) {
            this.lft = lft;
        }

        @Override
        public Integer getRgt() {
            return rgt;
        }

        @Override
        public void setRgt(Integer rgt) {
            this.rgt = rgt;
        }

        @Override
        public Integer getDepth() {
            return depth;
        }

        @Override
        public void setDepth(Integer depth) {
            this.depth = depth;
        }
    }

    private static class TestNodeComponent extends NodeComponent<TestNestedNode> {
        private TestNestedNode node;
        private NodeComponent<TestNestedNode> parent;
        private final Set<NodeComponent<TestNestedNode>> children = new LinkedHashSet<>();

        private TestNodeComponent(TestNestedNode node) {
            super(node);
        }

        @Override
        public TestNestedNode getNode() {
            return node;
        }

        @Override
        public void setNode(TestNestedNode node) {
            this.node = node;
        }

        @Override
        public Set<NodeComponent<TestNestedNode>> getChildren() {
            return children;
        }

        @Override
        public void addChild(NodeComponent<TestNestedNode> child) {
            this.children.add(child);
        }

        @Override
        public NodeComponent<TestNestedNode> getParent() {
            return parent;
        }

        @Override
        public void setParent(NodeComponent<TestNestedNode> parent) {
            this.parent = parent;
        }

        @Override
        public void print(String i) {
        }
    }
}
