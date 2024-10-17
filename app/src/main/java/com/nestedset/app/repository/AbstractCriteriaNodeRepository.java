package com.nestedset.app.repository;

import com.nestedset.library.annotation.*;
import com.nestedset.library.model.NodeComponent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractCriteriaNodeRepository<T extends NodeComponent, ID> implements NodeRepository<T, ID> {
    private final Class<T> entityClassType;
    private final EntityManager entityManager;
    private final NodeField configs;

    public AbstractCriteriaNodeRepository(Class<T> entityClassType, EntityManager entityManager) {
        this.entityClassType = entityClassType;
        this.entityManager = entityManager;
        configs = getConfig(entityClassType);
    }

    private NodeField getConfig(Class<T> clazz) {
        NodeField config = new NodeField();

        Map<Class<? extends Annotation>, Consumer<String>> annotationToSetter = new HashMap<>();
        annotationToSetter.put(Id.class, config::setIdFieldName);
        annotationToSetter.put(NameColumn.class, config::setNameFieldName);
        annotationToSetter.put(LeftColumn.class, config::setLeftFieldName);
        annotationToSetter.put(RightColumn.class, config::setRightFieldName);
        annotationToSetter.put(DepthColumn.class, config::setDepthFieldName);

        for (Field field : clazz.getDeclaredFields()) {
            for (Map.Entry<Class<? extends Annotation>, Consumer<String>> entry : annotationToSetter.entrySet()) {
                if (field.isAnnotationPresent(entry.getKey())) {
                    entry.getValue().accept(field.getName());
                    break;
                }
            }
        }

        return config;
    }

    protected List<T> executeQuery(CriteriaQuery<T> query) {
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Integer findMaxRight() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClassType);
        Root<T> queryRoot = cq.from(entityClassType);

        cq.orderBy(cb.desc(queryRoot.get(configs.getRightFieldName())));
        List<T> highestRows = executeQuery(cq);

        if (highestRows.isEmpty()) {
            return 0;
        } else {
            return highestRows.getFirst().getRgt();
        }
    }

    @Override
    public void incrementLeftBoundaryAfter(Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClassType);
        Root<T> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getLeftFieldName()), right);
        cq.where(predicate);

        List<T> result = entityManager.createQuery(cq).getResultList();

        for (T node : result) {
            node.setLft(node.getRgt() + 2);
            entityManager.merge(node);
        }
    }

    @Override
    public void incrementRightBoundaryAfter(Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClassType);
        Root<T> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(configs.getRightFieldName()), right);
        cq.where(predicate);

        List<T> result = entityManager.createQuery(cq).getResultList();

        for (T node : result) {
            node.setRgt(node.getRgt() + 2);
            entityManager.merge(node);
        }
    }

    @Override
    public void decrementLeftBoundaryAfter(Integer right, Integer width) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClassType);
        Root<T> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getLeftFieldName()), right);
        cq.where(predicate);

        List<T> result = entityManager.createQuery(cq).getResultList();

        for (T node : result) {
            node.setLft(node.getLft() - width);
            entityManager.merge(node);
        }
    }

    @Override
    public void decrementRightBoundaryAfter(Integer right, Integer width) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(entityClassType);
        Root<T> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getRightFieldName()), right);
        cq.where(predicate);

        List<T> result = entityManager.createQuery(cq).getResultList();

        for (T node : result) {
            node.setRgt(node.getRgt() - width);
            entityManager.merge(node);
        }
    }

    @Override
    public void deleteNodesInRange(Integer left, Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> query = criteriaBuilder.createCriteriaDelete(entityClassType);
        Root<T> root = query.from(entityClassType);

        query.where(
                criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(configs.getLeftFieldName()), left),
                        criteriaBuilder.lessThanOrEqualTo(root.get(configs.getRightFieldName()), right)
                )
        );
        entityManager.createQuery(query).executeUpdate();
    }

    @Override
    public List<T> findChildren(Integer left, Integer right) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClassType);
        Root<T> root = cq.from(entityClassType);

        Predicate leftPredicate = cb.greaterThanOrEqualTo(root.get(configs.getLeftFieldName()), left);
        Predicate rightPredicate = cb.lessThanOrEqualTo(root.get(configs.getRightFieldName()), right);
        cq.where(cb.and(leftPredicate, rightPredicate))
                .orderBy(cb.asc(root.get(configs.getLeftFieldName())));

        return executeQuery(cq);
    }

    /**
     * FIND THE IMMEDIATE SUBORDINATES OF A NODE
     * Imagine you are showing a T of electronics products on a retailer web site.
     * When a user clicks on a T, you would want to show the products of that T,
     * as well as list its immediate sub-categories, but not the entire tree of categories beneath it.
     * For this, we need to show the node and its immediate sub-nodes, but no further down the tree. For example,
     * when showing the PORTABLE ELECTRONICS T,
     * we will want to show MP3 PLAYERS, CD PLAYERS, and 2 WAY RADIOS, but not FLASH.
     * This can be easily accomplished by adding a HAVING clause to our previous query:
     */
    @Override
    public List<T> findImmediateChildren(ID nodeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClassType);

        Root<T> node = query.from(entityClassType);
        Root<T> parent = query.from(entityClassType);
        Root<T> subParent = query.from(entityClassType);

        Subquery<Integer> subQuery = query.subquery(Integer.class);
        Root<T> subNode = subQuery.from(entityClassType);
        Root<T> subParentNode = subQuery.from(entityClassType);

        subQuery.select(cb.diff(cb.count(subParentNode), 1).as(Integer.class))
                .where(
                        cb.between(subNode.get(configs.getLeftFieldName()), subParentNode.get(configs.getLeftFieldName()), subParentNode.get(configs.getRightFieldName())),
                        cb.equal(subNode.get(configs.getIdFieldName()), nodeId)
                )
                .groupBy(subNode.get(configs.getIdFieldName()), subNode.get(configs.getNameFieldName()), subNode.get(configs.getLeftFieldName()));


        query.multiselect(
                        node.get(configs.getIdFieldName()),
                        node.get(configs.getNameFieldName()),
                        node.get(configs.getLeftFieldName()),
                        node.get(configs.getRightFieldName()),
                        cb.diff(cb.count(parent), cb.sum(subQuery.getSelection(), 1)).as(Integer.class)
                )
                .where(
                        cb.between(node.get(configs.getLeftFieldName()), parent.get(configs.getLeftFieldName()), parent.get(configs.getRightFieldName())),
                        cb.between(node.get(configs.getLeftFieldName()), subParent.get(configs.getLeftFieldName()), subParent.get(configs.getRightFieldName())),
                        cb.equal(subParent.get(configs.getIdFieldName()), nodeId)
                )
                .groupBy(node.get(configs.getIdFieldName()), node.get(configs.getNameFieldName()), subQuery.getSelection(), node.get(configs.getLeftFieldName()))
                .having(cb.le(cb.diff(cb.count(parent), cb.sum(subQuery.getSelection(), 1)), 1))
                .orderBy(cb.asc(node.get(configs.getLeftFieldName())));

        return executeQuery(query);

    }

    @Override
    public List<T> findParentOf(ID id) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClassType);
        Root<T> parent = query.from(entityClassType);
        Root<T> node = query.from(entityClassType);

        Predicate leftBetween = criteriaBuilder.between(node.get(configs.getLeftFieldName()), parent.get(configs.getLeftFieldName()), parent.get(configs.getRightFieldName()));
        Predicate nodeIdMatch = criteriaBuilder.and(criteriaBuilder.equal(node.get(configs.getIdFieldName()), id));

        query.select(parent)
                .where(leftBetween, nodeIdMatch)
                .orderBy(criteriaBuilder.asc(parent.get(configs.getLeftFieldName())));

        return this.executeQuery(query);
    }

}
