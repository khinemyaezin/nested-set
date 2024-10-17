package com.nestedset.app.repository;

import com.nestedset.library.model.NodeComponent;

import java.util.List;

public interface NodeRepository<T extends NodeComponent,ID> {
    Integer findMaxRight();

    List<T> findChildren(Integer left, Integer right);

    void incrementLeftBoundaryAfter(Integer right);

    void incrementRightBoundaryAfter(Integer right);

    void decrementLeftBoundaryAfter(Integer right, Integer width);

    void decrementRightBoundaryAfter(Integer right, Integer width);

    void deleteNodesInRange(Integer left, Integer right);

    List<T> findImmediateChildren(ID nodeId);

    List<T> findParentOf(ID id);
}
