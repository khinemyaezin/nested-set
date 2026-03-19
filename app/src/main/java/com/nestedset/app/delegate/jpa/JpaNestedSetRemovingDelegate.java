package com.nestedset.app.delegate.jpa;

import com.nestedset.app.config.JpaNestedSetRepositoryConfiguration;
import com.nestedset.app.delegate.NestedSetRemovingDelegate;
import com.nestedset.library.model.NestedSet;
import jakarta.persistence.criteria.*;

import java.util.List;

public class JpaNestedSetRemovingDelegate<N extends NestedSet<ID>,ID> extends JpaNestedSetDelegate<N,ID> implements NestedSetRemovingDelegate<N,ID> {

    public JpaNestedSetRemovingDelegate(JpaNestedSetRepositoryConfiguration<N,ID> nestedSetRepositoryConfiguration) {
        super(nestedSetRepositoryConfiguration);
    }

    @Override
    public void removeNodesInRange(Integer left, Integer right) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<N> query = criteriaBuilder.createCriteriaDelete(entityClassType);
        Root<N> root = query.from(entityClassType);

        query.where(
                criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get(configs.getLeftFieldName()), left),
                        criteriaBuilder.lessThanOrEqualTo(root.get(configs.getRightFieldName()), right)
                )
        );
        entityManager.createQuery(query).executeUpdate();
    }

    @Override
    public void decrementLeftBoundaryAfter(Integer right, Integer width) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> cq = criteriaBuilder.createQuery(entityClassType);
        Root<N> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getLeftFieldName()), right);
        cq.where(predicate);

        List<N> result = entityManager.createQuery(cq).getResultList();

        for (N node : result) {
            node.setLft(node.getLft() - width);
            entityManager.merge(node);
        }
    }

    @Override
    public void decrementRightBoundaryAfter(Integer right, Integer width) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<N> cq = criteriaBuilder.createQuery(entityClassType);
        Root<N> root = cq.from(entityClassType);
        Predicate predicate = criteriaBuilder.greaterThan(root.get(configs.getRightFieldName()), right);
        cq.where(predicate);

        List<N> result = entityManager.createQuery(cq).getResultList();

        for (N node : result) {
            node.setRgt(node.getRgt() - width);
            entityManager.merge(node);
        }
    }

}
