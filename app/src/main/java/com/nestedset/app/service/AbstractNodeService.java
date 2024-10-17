package com.nestedset.app.service;

import com.nestedset.app.repository.JpaNodeRepository;
import com.nestedset.library.model.NodeComponent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class AbstractNodeService<T extends NodeComponent, ID> implements NodeService<T, ID> {
    private final JpaNodeRepository<T, ID> jpaNodeRepository;

    protected AbstractNodeService(JpaNodeRepository<T, ID> jpaNodeRepository) {
        this.jpaNodeRepository = jpaNodeRepository;
    }

    @Override
    @Transactional
    public T createNode(T entity) {
        Integer right = jpaNodeRepository.findMaxRight();
        if (right == null) {
            right = 0;
        }
        right++;

        entity.setLft(right);
        entity.setRgt(right + 1);
        entity.setDepth(0);

        return jpaNodeRepository.save(entity);
    }

    @Transactional
    @Override
    public T updateNode(ID id, T entity) {
        entity = jpaNodeRepository.findById(id).orElseThrow(() -> new RuntimeException("Node is not found"));
        return jpaNodeRepository.save(entity);
    }

    @Override
    public Optional<T> readNode(ID id) {
        return jpaNodeRepository.findById(id);
    }

    @Override
    @Transactional
    public T createNode(T entity, ID parentId) {
        T rootNode = jpaNodeRepository.findById(parentId).orElseThrow(() -> new RuntimeException("Parent not found"));
        Integer right = rootNode.getRgt();

        jpaNodeRepository.incrementLeftBoundaryAfter(right);
        jpaNodeRepository.incrementRightBoundaryAfter(right);

        entity.setLft(right);
        entity.setRgt(right + 1);
        entity.setDepth(rootNode.getDepth() + 1);

        return jpaNodeRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteNode(ID id) {
        T category = jpaNodeRepository.findById(id).orElseThrow(() -> new RuntimeException("Node not found"));
        Integer left = category.getLft();
        Integer right = category.getRgt();
        Integer width = right - left + 1;

        jpaNodeRepository.deleteNodesInRange(left, right);

        jpaNodeRepository.decrementRightBoundaryAfter(right, width);
        jpaNodeRepository.decrementLeftBoundaryAfter(right, width);
    }

    @Override
    public List<T> findImmediateChildren(ID nodeId) {
        return this.jpaNodeRepository.findImmediateChildren(nodeId);
    }

    @Override
    public List<T> findParentOf(ID id) {
        return this.jpaNodeRepository.findParentOf(id);
    }
}