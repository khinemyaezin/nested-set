package com.nestedset.library.model;

public interface NestedSet<ID> {
    ID getId();

    void setId(ID id);

    Integer getLft();

    void setLft(Integer lft);

    Integer getRgt();

    void setRgt(Integer rgt);

    Integer getDepth();

    void setDepth(Integer depth);
}
