// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractEntityType<TKey, TEntity extends Entity<TKey>> implements EntityType<TKey, TEntity> {
    private final String name;
    private final Class<TEntity> entityClass;
    private final ValueField<TEntity, TKey> keyField;
    private final List<Field<TEntity, ?>> fields = new ArrayList<>();
    private final List<EntityType> relatedEntities = new ArrayList<>();

    protected AbstractEntityType(
            String name,
            Class<TEntity> entityClass,
            ValueField<TEntity, TKey> keyField, Field<TEntity, ?>... otherFields) {
        this.name = name;
        this.entityClass = entityClass;
        this.keyField = keyField;
        addFields(keyField);
        addFields(otherFields);
    }

    @Override
    public Class<TEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Collection<Field<TEntity, ?>> getFields() {
        return fields;
    }

    @Override
    public ValueField<TEntity, TKey> getKeyField() {
        return keyField;
    }

    @Override
    public Collection<EntityType> getRelatedEntities() {
        return relatedEntities;
    }

    @Override
    public TKey getKey(TEntity entity) {
        return entity.getEntityId();
    }

    private AbstractEntityType<TKey, TEntity> addFields(Field<TEntity, ?>... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }
}
