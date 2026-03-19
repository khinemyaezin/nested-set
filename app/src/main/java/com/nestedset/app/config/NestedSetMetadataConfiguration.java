package com.nestedset.app.config;

import com.nestedset.library.annotation.DepthColumn;
import com.nestedset.library.annotation.LeftColumn;
import com.nestedset.library.annotation.NameColumn;
import com.nestedset.library.annotation.RightColumn;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeField;
import jakarta.persistence.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NestedSetMetadataConfiguration<N extends NestedSet<ID>, ID> {
    private final NodeField configs;
    private final Class<N> entityClassType;

    public NestedSetMetadataConfiguration(Class<N> entityClassType) {
        this.entityClassType = entityClassType;
        this.configs = getConfig(entityClassType);
    }

    public NodeField getConfigs() {
        return configs;
    }

    public Class<N> getEntityClassType() {
        return entityClassType;
    }

    private NodeField getConfig(Class<?> clazz) {
        NodeField config = new NodeField();

        Map<Class<? extends Annotation>, Consumer<String>> annotationToSetter = new HashMap<>();
        annotationToSetter.put(Id.class, config::setIdFieldName);
        annotationToSetter.put(NameColumn.class, config::setNameFieldName);
        annotationToSetter.put(LeftColumn.class, config::setLeftFieldName);
        annotationToSetter.put(RightColumn.class, config::setRightFieldName);
        annotationToSetter.put(DepthColumn.class, config::setDepthFieldName);

        for (Class<?> current = clazz; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                for (Map.Entry<Class<? extends Annotation>, Consumer<String>> entry : annotationToSetter.entrySet()) {
                    if (field.isAnnotationPresent(entry.getKey()) && !isFieldConfigured(config, entry.getKey())) {
                        entry.getValue().accept(field.getName());
                        break;
                    }
                }
            }
        }

        return config;
    }

    private boolean isFieldConfigured(NodeField config, Class<? extends Annotation> annotation) {
        if (annotation == Id.class) {
            return config.getIdFieldName() != null;
        }
        if (annotation == NameColumn.class) {
            return config.getNameFieldName() != null;
        }
        if (annotation == LeftColumn.class) {
            return config.getLeftFieldName() != null;
        }
        if (annotation == RightColumn.class) {
            return config.getRightFieldName() != null;
        }
        if (annotation == DepthColumn.class) {
            return config.getDepthFieldName() != null;
        }
        return false;
    }
}
