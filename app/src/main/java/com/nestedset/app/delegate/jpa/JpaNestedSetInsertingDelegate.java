package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import com.nestedset.app.delegate.NestedSetInsertingDelegate;
import com.nestedset.library.model.NestedSet;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class JpaNestedSetInsertingDelegate<N extends NestedSet<ID>,ID> extends JpaNestedSetDelegate<N,ID> implements NestedSetInsertingDelegate<N,ID> {

    public JpaNestedSetInsertingDelegate(JpaNestedSetRepositoryConfiguration<N,ID> nestedSetRepositoryConfiguration) {
        super(nestedSetRepositoryConfiguration);
    }

    @Override
    public void insert(N node) {
        super.entityManager.persist(node);
    }

    @Override
    public void incrementLeftBoundaryAfter(Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> cq = criteriaBuilder.createQuery(entityClassType);
        Root<N> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getLeftFieldName()), right);
        cq.where(predicate);

        List<N> result = entityManager.createQuery(cq).getResultList();

        for (N node : result) {
            node.setLft(node.getRgt() + 2);
            entityManager.merge(node);
        }
    }

    @Override
    public void incrementRightBoundaryAfter(Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> cq = criteriaBuilder.createQuery(entityClassType);
        Root<N> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(configs.getRightFieldName()), right);
        cq.where(predicate);

        List<N> result = entityManager.createQuery(cq).getResultList();

        for (N node : result) {
            node.setRgt(node.getRgt() + 2);
            entityManager.merge(node);
        }
    }

    @Override
    public Integer getMaxRight() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> cq = cb.createQuery(entityClassType);
        Root<N> queryRoot = cq.from(entityClassType);

        cq.orderBy(cb.desc(queryRoot.get(configs.getRightFieldName())));
        List<N> highestRows = entityManager.createQuery(cq).getResultList();

        if (highestRows.isEmpty()) {
            return 0;
        } else {
            return highestRows.getFirst().getRgt();
        }
    }

}
