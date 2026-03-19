package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = TestDatabaseConfig.class)
class JpaNestedSetRetrievingDelegateTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaContext jpaContext;

    private JpaNestedSetRetrievingDelegate<TestNode, Long> delegate;

    @BeforeEach
    void setUp() {
        JpaNestedSetRepositoryConfiguration<TestNode, Long> config =
                new JpaNestedSetRepositoryConfiguration<>(jpaContext, TestNode.class);
        delegate = new JpaNestedSetRetrievingDelegate<>(config);
    }

    @Test
    void getParentOf_shouldReturnsImmediateParent() {
        TestNode root   = new TestNode(1L, "root",   1, 6, 0);
        TestNode parent = new TestNode(2L, "parent", 2, 5, 1);
        TestNode child  = new TestNode(3L, "child",  3, 4, 2);

        entityManager.persist(root);
        entityManager.persist(parent);
        entityManager.persist(child);
        entityManager.flush();

        Optional<TestNode> result = delegate.getParentOf(child);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getId());
        assertEquals("parent", result.get().getName());
    }

    @Test
    void getParentOf_shouldReturnsEmpty() {
        TestNode root = new TestNode(1L, "root", 1, 2, 0);

        entityManager.persist(root);
        entityManager.flush();

        Optional<TestNode> result = delegate.getParentOf(root);

        assertTrue(result.isEmpty());
    }

    @Test
    void getParentOf_shouldReturnsDirectParentInDeepTree() {
        // Root(1,10) -> A(2,9) -> B(3,8) -> Leaf(4,5)
        TestNode root = new TestNode(1L, "root", 1, 10, 0);
        TestNode a    = new TestNode(2L, "A",    2,  9, 1);
        TestNode b    = new TestNode(3L, "B",    3,  8, 2);
        TestNode leaf = new TestNode(4L, "leaf", 4,  5, 3);

        entityManager.persist(root);
        entityManager.persist(a);
        entityManager.persist(b);
        entityManager.persist(leaf);
        entityManager.flush();

        Optional<TestNode> result = delegate.getParentOf(leaf);

        assertTrue(result.isPresent());
        assertEquals(3L, result.get().getId());
        assertEquals("B", result.get().getName());
    }

    @Test
    void getImmediateChildren_shouldReturnOnlyImmediateChildren() {
        // Root(1,10) -> A(2,5) -> C(3,4), Root -> B(6,9) -> D(7,8)
        TestNode root = new TestNode(1L, "root", 1, 10, 0);
        TestNode a    = new TestNode(2L, "A",    2,  5, 1);
        TestNode c    = new TestNode(3L, "C",    3,  4, 2);
        TestNode b    = new TestNode(4L, "B",    6,  9, 1);
        TestNode d    = new TestNode(5L, "D",    7,  8, 2);

        entityManager.persist(root);
        entityManager.persist(a);
        entityManager.persist(c);
        entityManager.persist(b);
        entityManager.persist(d);
        entityManager.flush();

        List<TestNode> result = delegate.getImmediateChildren(root);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("A", result.get(0).getName());
        assertEquals(4L, result.get(1).getId());
        assertEquals("B", result.get(1).getName());
    }

    @Test
    void getLeafNodes_shouldReturnOnlyLeavesInRequestedSubtree() {
        // Root(1,10) -> A(2,5) -> C(3,4), Root -> B(6,9) -> D(7,8)
        TestNode root = new TestNode(1L, "root", 1, 10, 0);
        TestNode a    = new TestNode(2L, "A",    2,  5, 1);
        TestNode c    = new TestNode(3L, "C",    3,  4, 2);
        TestNode b    = new TestNode(4L, "B",    6,  9, 1);
        TestNode d    = new TestNode(5L, "D",    7,  8, 2);

        entityManager.persist(root);
        entityManager.persist(a);
        entityManager.persist(c);
        entityManager.persist(b);
        entityManager.persist(d);
        entityManager.flush();

        List<TestNode> result = delegate.getLeafNodes(root);

        assertEquals(2, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals("C", result.get(0).getName());
        assertEquals(5L, result.get(1).getId());
        assertEquals("D", result.get(1).getName());
    }

    @Test
    void getTreeAsList_shouldReturnOnlyRequestedSubtree() {
        // Root(1,10) -> A(2,5) -> C(3,4), Root -> B(6,9) -> D(7,8)
        TestNode root = new TestNode(1L, "root", 1, 10, 0);
        TestNode a    = new TestNode(2L, "A",    2,  5, 1);
        TestNode c    = new TestNode(3L, "C",    3,  4, 2);
        TestNode b    = new TestNode(4L, "B",    6,  9, 1);
        TestNode d    = new TestNode(5L, "D",    7,  8, 2);

        entityManager.persist(root);
        entityManager.persist(a);
        entityManager.persist(c);
        entityManager.persist(b);
        entityManager.persist(d);
        entityManager.flush();

        List<TestNode> result = delegate.getSubtreeAsList(b);

        assertEquals(2, result.size());
        assertEquals(4L, result.get(0).getId());
        assertEquals("B", result.get(0).getName());
        assertEquals(5L, result.get(1).getId());
        assertEquals("D", result.get(1).getName());
    }
}
