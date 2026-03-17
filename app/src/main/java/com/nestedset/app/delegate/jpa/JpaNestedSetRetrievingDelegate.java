package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.NestedSetRepositoryConfiguration;
import com.nestedset.app.delegate.NestedSetRetrievingDelegate;
import com.nestedset.library.model.NestedSet;
import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Optional;

public class JpaNestedSetRetrievingDelegate<N extends NestedSet<ID>,ID> extends JpaNestedSetDelegate<N,ID> implements NestedSetRetrievingDelegate<N,ID> {

    public JpaNestedSetRetrievingDelegate(NestedSetRepositoryConfiguration<N,ID> nestedSetRepositoryConfiguration) {
        super(nestedSetRepositoryConfiguration);
    }

    @Override
    public List<N> getImmediateChildren(N n) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> query = cb.createQuery(entityClassType);

        Root<N> node = query.from(entityClassType);
        Root<N> parent = query.from(entityClassType);
        Root<N> subParent = query.from(entityClassType);

        Subquery<Integer> subQuery = query.subquery(Integer.class);
        Root<N> subNode = subQuery.from(entityClassType);
        Root<N> subParentNode = subQuery.from(entityClassType);

        subQuery.select(cb.diff(cb.count(subParentNode), 1).as(Integer.class))
                .where(
                        cb.between(
                                subNode.get(configs.getLeftFieldName()),
                                subParentNode.get(configs.getLeftFieldName()),
                                subParentNode.get(configs.getRightFieldName())
                        ),
                        cb.equal(subNode.get(configs.getIdFieldName()), n.getId())
                )
                .groupBy(
                        subNode.get(configs.getIdFieldName()),
                        subNode.get(configs.getNameFieldName()),
                        subNode.get(configs.getLeftFieldName())
                );

        query.select(node)
                .where(
                        cb.between(
                                node.get(configs.getLeftFieldName()),
                                parent.get(configs.getLeftFieldName()),
                                parent.get(configs.getRightFieldName())
                        ),
                        cb.between(
                                node.get(configs.getLeftFieldName()),
                                subParent.get(configs.getLeftFieldName()),
                                subParent.get(configs.getRightFieldName())
                        ),
                        cb.equal(subParent.get(configs.getIdFieldName()), n.getId()),
                        cb.notEqual(node.get(configs.getIdFieldName()), n.getId())
                )
                .groupBy(
                        node.get(configs.getIdFieldName()),
                        node.get(configs.getNameFieldName()),
                        node.get(configs.getLeftFieldName()),
                        node.get(configs.getRightFieldName()),
                        subQuery.getSelection()
                )
                .having(
                        cb.le(
                                cb.diff(cb.count(parent), cb.sum(subQuery.getSelection(), 1)),
                                1
                        )
                )
                .orderBy(cb.asc(node.get(configs.getLeftFieldName())));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public Optional<N> getParentOf(N n) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> query = criteriaBuilder.createQuery(entityClassType);
        Root<N> parent = query.from(entityClassType);
        Root<N> node = query.from(entityClassType);

        Predicate leftBetween = criteriaBuilder.between(node.get(configs.getLeftFieldName()), parent.get(configs.getLeftFieldName()), parent.get(configs.getRightFieldName()));
        Predicate nodeIdMatch = criteriaBuilder.equal(node.get(configs.getIdFieldName()), n.getId());
        Predicate parentNotNode = criteriaBuilder.notEqual(parent.get(configs.getIdFieldName()), node.get(configs.getIdFieldName()));

        query.select(parent)
                .where(leftBetween, nodeIdMatch, parentNotNode)
                .orderBy(criteriaBuilder.desc(parent.get(configs.getLeftFieldName())));
        List<N> results = entityManager.createQuery(query).setMaxResults(1).getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<N> getLeafNodes(N node) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> query = cb.createQuery(entityClassType);
        Root<N> root = query.from(entityClassType);

        Expression<Integer> left = root.get(configs.getLeftFieldName()).as(Integer.class);
        Expression<Integer> right = root.get(configs.getRightFieldName()).as(Integer.class);

        query.select(root)
                .where(
                        cb.between(left, node.getLft(), node.getRgt()),
                        cb.equal(right, cb.sum(left, 1))
                )
                .orderBy(cb.asc(left));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<N> getTreeAsList() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> select = cb.createQuery(entityClassType);
        Root<N> root = select.from(entityClassType);
        select.orderBy(cb.asc(root.get(configs.getLeftFieldName())));

        return entityManager.createQuery(select).getResultList();
    }

    @Override
    public List<N> getSubtreeAsList(N node) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> select = cb.createQuery(entityClassType);
        Root<N> root = select.from(entityClassType);
        select.select(root)
                .where(cb.between(root.get(configs.getLeftFieldName()), node.getLft(), node.getRgt()))
                .orderBy(cb.asc(root.get(configs.getLeftFieldName())));

        return  entityManager.createQuery(select).getResultList();
    }
}
