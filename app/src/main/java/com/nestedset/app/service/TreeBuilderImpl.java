package com.nestedset.app.service;

import com.nestedset.app.config.NodeComponentFactory;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeComponent;

import java.util.*;
import java.util.stream.Collectors;

public class TreeBuilderImpl<T extends NestedSet<ID>, ID> implements TreeBuilder<T, ID> {
    private final NodeComponentFactory<T, ID> nodeComponentFactory;

    public TreeBuilderImpl(NodeComponentFactory<T, ID> nodeComponentFactory) {
        this.nodeComponentFactory = nodeComponentFactory;
    }

    @Override
    public NodeComponent<T> buildTree(List<T> nodeList) {
        T root = nodeList.getFirst();
        NodeComponent<T> node;
        if (root.getRgt() == root.getLft() + 1) {
            node = nodeComponentFactory.createLeafNodeComponent(root);
            return node;
        } else {
            node = nodeComponentFactory.createCompositeNodeComponent(root);
            return buildTreeRecursive(node, nodeList, 0);
        }
    }

    @Override
    public NodeComponent<T> buildTree(Collection<T> input) {
        List<T> nodeList = input.stream().sorted(Comparator.comparingInt(NestedSet::getLft)) // Sorting by ascending lft
                .collect(Collectors.toList());

        return this.buildTree(nodeList);
    }


    private NodeComponent<T> buildTreeRecursive(NodeComponent<T> parent, List<T> nodeList, int index) {
        int i = index + 1;
        while (i < nodeList.size() && nodeList.get(i).getLft() < parent.getNode().getRgt()) {
            T child = nodeList.get(i);
            if (child.getDepth() == parent.getNode().getDepth() + 1) {
                NodeComponent<T> node;
                if (child.getRgt() == child.getLft() + 1) {
                    node = nodeComponentFactory.createLeafNodeComponent(child);
                } else {
                    node = nodeComponentFactory.createCompositeNodeComponent(child);
                }
                //this.merge(child, node);
                node.setParent(parent);
                parent.addChild(buildTreeRecursive(node, nodeList, i));
            }
            i++;
        }
        return parent;
    }

    @Override
    public List<NodeComponent<T>> getLeafList(NodeComponent<T> node) {
        List<NodeComponent<T>> leafNodes = new ArrayList<>();

        // Base case: If the node is null, return an empty list
        if (node == null) {
            return leafNodes;
        }

        if (node.getNode().getRgt() == node.getNode().getLft() + 1) {
            leafNodes.add(node);
        } else {
            for (NodeComponent<T> child : node.getChildren()) {
                leafNodes.addAll(getLeafList(child));
            }
        }
        return leafNodes;
    }


}
