package com.nestedset.app;

import com.nestedset.app.service.NestedSetNodeInserter;
import com.nestedset.app.service.NestedSetNodeRemover;
import com.nestedset.app.service.NestedSetNodeRetriever;
import com.nestedset.app.service.TreeBuilder;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DelegatingNestedSetNodeRepository<N extends NestedSet<ID>,ID> implements NestedSetNodeRepository<N,ID> {
    protected final NestedSetNodeInserter<N,ID> inserter;
    protected final NestedSetNodeRemover<N,ID> remover;
    protected final NestedSetNodeRetriever<N,ID> retriever;
    protected final TreeBuilder<N,ID> treeBuilder;

    public DelegatingNestedSetNodeRepository(NestedSetNodeInserter<N, ID> inserter, NestedSetNodeRemover<N, ID> remover, NestedSetNodeRetriever<N, ID> retriever, TreeBuilder<N, ID> treeBuilder) {
        this.inserter = inserter;
        this.remover = remover;
        this.retriever = retriever;
        this.treeBuilder = treeBuilder;
    }


    @Override
    public void insertAsFirstRoot(N node) {
        this.inserter.createAsRoot(node);
    }

    @Override
    public void insertAsLastChildOf(N node, N parent) {
        this.inserter.createAsLastOf(node,parent);
    }

    @Override
    public void removeSubtree(N node) {
        this.remover.deleteNode(node);
    }

    @Override
    public NodeComponent<N> getImmediateChildren(N node) {
        List<N> children = this.retriever.findImmediateChildren(node);
        List<N> nodesWithParent = new ArrayList<>(children.size() + 1);
        nodesWithParent.add(node);
        nodesWithParent.addAll(children);
        return this.treeBuilder.buildTree(nodesWithParent);
    }

    @Override
    public Optional<N> getParent(N node) {
        return this.retriever.findParentOf(node);
    }

    @Override
    public NodeComponent<N> getTree(N node) {
        var nodeList = this.retriever.findTreeAsList();
        return this.treeBuilder.buildTree(nodeList);
    }

    @Override
    public NodeComponent<N> getSubtreeOf(N node) {
        var nodeList = this.retriever.getSubtreeAsList(node);
        return this.treeBuilder.buildTree(nodeList);
    }

}
