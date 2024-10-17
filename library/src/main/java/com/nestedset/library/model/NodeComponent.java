package com.nestedset.library.model;

import java.util.Set;

public interface NodeComponent {
    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    Integer getLft();

    void setLft(Integer lft);

    Integer getRgt();

    void setRgt(Integer rgt);

    Integer getDepth();

    void setDepth(Integer depth);

    Set<NodeComponent> getChildren();

    void setChildren(Set<NodeComponent> children);

    void addSubNode(NodeComponent child);

    NodeComponent getParent() ;

    void setParent(NodeComponent parent);

    void print(String i) ;
}
