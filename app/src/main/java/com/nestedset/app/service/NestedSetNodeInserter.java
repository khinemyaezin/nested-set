package com.nestedset.app.service;

import com.nestedset.library.model.NestedSet;

import java.io.Serializable;

public interface NestedSetNodeInserter<T extends NestedSet<ID>, ID> extends Serializable {

    T createAsRoot(T entity);

    T createAsLastOf(T entity, T rootNode);
}
