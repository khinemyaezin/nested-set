package com.nestedset.app.config;

import com.nestedset.library.annotation.DepthColumn;
import com.nestedset.library.annotation.LeftColumn;
import com.nestedset.library.annotation.NameColumn;
import com.nestedset.library.annotation.RightColumn;
import com.nestedset.library.model.NestedSet;
import com.nestedset.library.model.NodeField;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NestedSetRepositoryConfiguration<N extends NestedSet<ID>,ID> {
    private final EntityManager entityManager;
    private final NodeField configs;
    private final Class<N> entityClassType;

    public NestedSetRepositoryConfiguration(JpaContext context, Class<N> entityClassType) {
        this.entityManager = context.getEntityManagerByManagedType(entityClassType);
        this.entityClassType = entityClassType;
        configs = getConfig(entityClassType);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public NodeField getConfigs() {
        return configs;
    }

    public Class<N> getEntityClassType() {
        return entityClassType;
    }

    private NodeField getConfig(Class<N> clazz) {
        NodeField config = new NodeField();

        Map<Class<? extends Annotation>, Consumer<String>> annotationToSetter = new HashMap<>();
        annotationToSetter.put(Id.class, config::setIdFieldName);
        annotationToSetter.put(NameColumn.class, config::setNameFieldName);
        annotationToSetter.put(LeftColumn.class, config::setLeftFieldName);
        annotationToSetter.put(RightColumn.class, config::setRightFieldName);
        annotationToSetter.put(DepthColumn.class, config::setDepthFieldName);

        for (Field field : clazz.getDeclaredFields()) {
            for (Map.Entry<Class<? extends Annotation>, Consumer<String>> entry : annotationToSetter.entrySet()) {
                if (field.isAnnotationPresent(entry.getKey())) {
                    entry.getValue().accept(field.getName());
                    break;
                }
            }
        }

        return config;
    }
}
