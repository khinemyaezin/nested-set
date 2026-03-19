package com.nestedset.app.config;

import com.nestedset.library.annotation.DepthColumn;
import com.nestedset.library.annotation.LeftColumn;
import com.nestedset.library.annotation.NameColumn;
import com.nestedset.library.annotation.RightColumn;
import com.nestedset.library.model.NestedSet;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NestedSetMetadataConfigurationTest {

    @Test
    void shouldResolveAnnotatedFieldsFromParentClassHierarchy() {
        NestedSetMetadataConfiguration<ChildNode, Long> config =
                new NestedSetMetadataConfiguration<>(ChildNode.class);

        assertEquals("id", config.getConfigs().getIdFieldName());
        assertEquals("name", config.getConfigs().getNameFieldName());
        assertEquals("lft", config.getConfigs().getLeftFieldName());
        assertEquals("rgt", config.getConfigs().getRightFieldName());
        assertEquals("depth", config.getConfigs().getDepthFieldName());
    }

    static abstract class BaseNode implements NestedSet<Long> {
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

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
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

    static class ChildNode extends BaseNode {
    }
}
