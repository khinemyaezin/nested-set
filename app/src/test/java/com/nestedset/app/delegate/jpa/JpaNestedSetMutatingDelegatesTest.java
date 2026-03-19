package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeInserter;
import com.nestedset.app.service.query.QueryBasedNestedSetNodeRemover;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = TestDatabaseConfig.class)
class JpaNestedSetMutatingDelegatesTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaContext jpaContext;

    private QueryBasedNestedSetNodeInserter<TestNode, Long> inserter;
    private QueryBasedNestedSetNodeRemover<TestNode, Long> remover;

    @BeforeEach
    void setUp() {
        JpaNestedSetRepositoryConfiguration<TestNode, Long> config =
                new JpaNestedSetRepositoryConfiguration<>(jpaContext, TestNode.class);
        inserter = new QueryBasedNestedSetNodeInserter<>(new JpaNestedSetInsertingDelegate<>(config));
        remover = new QueryBasedNestedSetNodeRemover<>(new JpaNestedSetRemovingDelegate<>(config));
    }

    @Test
    void createAsRoot_shouldCreateRootWhenTreeIsEmpty() {
        TestNode root = new TestNode(1L, "root", null, null, null);

        inserter.createAsRoot(root);

        entityManager.flush();
        entityManager.clear();

        TestNode savedRoot = entityManager.find(TestNode.class, 1L);
        assertNotNull(savedRoot);
        assertEquals(1, savedRoot.getLft());
        assertEquals(2, savedRoot.getRgt());
        assertEquals(0, savedRoot.getDepth());
    }

    @Test
    void createAsRoot_shouldThrowWhenRootAlreadyExists() {
        TestNode existingRoot = new TestNode(1L, "existing-root", 1, 2, 0);
        entityManager.persist(existingRoot);
        entityManager.flush();

        TestNode newRoot = new TestNode(2L, "new-root", null, null, null);

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> inserter.createAsRoot(newRoot));

        assertEquals("Root node already exists. Only one root node is allowed.", exception.getMessage());
        assertNull(entityManager.find(TestNode.class, 2L));
    }

    @Test
    void createAsLastOf_shouldShiftBoundariesAndInsertNode() {
        TestNode root1 = new TestNode(1L, "root1", 1, 6, 0);
        TestNode a = new TestNode(2L, "A", 2, 3, 1);
        TestNode b = new TestNode(3L, "B", 4, 5, 1);
        TestNode root2 = new TestNode(4L, "root2", 7, 8, 0);

        entityManager.persist(root1);
        entityManager.persist(a);
        entityManager.persist(b);
        entityManager.persist(root2);
        entityManager.flush();

        TestNode inserted = new TestNode(5L, "new-last-child", null, null, null);
        inserter.createAsLastOf(inserted, root1);

        entityManager.flush();
        entityManager.clear();

        TestNode updatedRoot1 = entityManager.find(TestNode.class, 1L);
        TestNode updatedA = entityManager.find(TestNode.class, 2L);
        TestNode updatedB = entityManager.find(TestNode.class, 3L);
        TestNode updatedRoot2 = entityManager.find(TestNode.class, 4L);
        TestNode newChild = entityManager.find(TestNode.class, 5L);

        assertNotNull(newChild);
        assertEquals(1, updatedRoot1.getLft());
        assertEquals(8, updatedRoot1.getRgt());
        assertEquals(2, updatedA.getLft());
        assertEquals(3, updatedA.getRgt());
        assertEquals(4, updatedB.getLft());
        assertEquals(5, updatedB.getRgt());
        assertEquals(6, newChild.getLft());
        assertEquals(7, newChild.getRgt());
        assertEquals(1, newChild.getDepth());
        assertEquals(9, updatedRoot2.getLft());
        assertEquals(10, updatedRoot2.getRgt());
    }

    @Test
    void deleteNode_shouldDeleteSubtreeAndCloseGap() {
        TestNode root = new TestNode(1L, "root", 1, 10, 0);
        TestNode a = new TestNode(2L, "A", 2, 5, 1);
        TestNode c = new TestNode(3L, "C", 3, 4, 2);
        TestNode b = new TestNode(4L, "B", 6, 9, 1);
        TestNode d = new TestNode(5L, "D", 7, 8, 2);

        entityManager.persist(root);
        entityManager.persist(a);
        entityManager.persist(c);
        entityManager.persist(b);
        entityManager.persist(d);
        entityManager.flush();

        remover.deleteNode(b);

        entityManager.flush();
        entityManager.clear();

        TestNode updatedRoot = entityManager.find(TestNode.class, 1L);
        TestNode updatedA = entityManager.find(TestNode.class, 2L);
        TestNode updatedC = entityManager.find(TestNode.class, 3L);
        TestNode deletedB = entityManager.find(TestNode.class, 4L);
        TestNode deletedD = entityManager.find(TestNode.class, 5L);

        assertNotNull(updatedRoot);
        assertNotNull(updatedA);
        assertNotNull(updatedC);
        assertNull(deletedB);
        assertNull(deletedD);
        assertEquals(1, updatedRoot.getLft());
        assertEquals(6, updatedRoot.getRgt());
        assertEquals(2, updatedA.getLft());
        assertEquals(5, updatedA.getRgt());
        assertEquals(3, updatedC.getLft());
        assertEquals(4, updatedC.getRgt());
    }
}
