package com.nestedset.app.delegate.jpa;

import com.nestedset.library.annotation.DepthColumn;
import com.nestedset.library.annotation.LeftColumn;
import com.nestedset.library.annotation.NameColumn;
import com.nestedset.library.annotation.RightColumn;
import com.nestedset.library.model.NestedSet;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "test_nodes")
public class TestNode implements NestedSet<Long> {
    @Id
    private Long id;

    @NameColumn
    private String name;

    @LeftColumn
    private Integer lft;

    @RightColumn
    private Integer rgt;

    @DepthColumn
    private Integer depth;

    public TestNode() {
    }

    public TestNode(Long id, String name, Integer lft, Integer rgt, Integer depth) {
        this.id = id;
        this.name = name;
        this.lft = lft;
        this.rgt = rgt;
        this.depth = depth;
    }

    @Override public Long    getId()    { return id; }
    @Override public void    setId(Long id)       { this.id = id; }
    @Override public Integer getLft()   { return lft; }
    @Override public void    setLft(Integer lft)  { this.lft = lft; }
    @Override public Integer getRgt()   { return rgt; }
    @Override public void    setRgt(Integer rgt)  { this.rgt = rgt; }
    @Override public Integer getDepth() { return depth; }
    @Override public void    setDepth(Integer depth) { this.depth = depth; }
    public    String  getName()  { return name; }
    public    void    setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestNode testNode = (TestNode) o;
        return id.equals(testNode.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
