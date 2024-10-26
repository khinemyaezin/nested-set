package com.nestedset.app.service.infra;

import com.nestedset.app.service.domain.ICategory;
import com.nestedset.library.model.NestedSet;

public class Entity implements NestedSet<Long>, ICategory {
    private Long id;
    private String uuid;
    private String name;
    private Integer lft;
    private Integer rgt;
    private Integer depth;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getLft() {
        return lft;
    }

    @Override
    public void setLft(Integer lft) {
        this.lft = lft;
    }

    @Override
    public Integer getRgt() {
        return rgt;
    }

    @Override
    public void setRgt(Integer rgt) {
        this.rgt = rgt;
    }

    @Override
    public Integer getDepth() {
        return depth;
    }

    @Override
    public void setDepth(Integer depth) {
        this.depth = depth;
    }
}
