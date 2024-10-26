package com.nestedset.app.service;


import com.nestedset.library.model.NestedSet;

public interface NestedSetNodeRemover<N extends NestedSet<ID>,ID> {

    void deleteNode(N node);

}
