// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public abstract class AbstractEntityType<TKey, TEntity> implements EntityType<TKey, TEntity> {
    private final String name;
    private final Class<TEntity> entityClass;
    private final ValueField<TEntity, TKey> keyField;
    private final List<Field<TEntity, ?>> fields = new ArrayList<>();
    private final List<RelationalField<TEntity, ?>> relationalFields = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected AbstractEntityType(
            Class<TEntity> entityClass,
            ValueField<TEntity, TKey> keyField, Field<TEntity, ?>... otherFields) {
        this.name = entityClass.getSimpleName();
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
    public Collection<RelationalField<TEntity, ?>> getRelationalFields() {
        return relationalFields;
    }

    @Override
    public TEntity newInstance(FieldValueLookup<TEntity> lookup) {
        TEntity entity = newInstance();
        for (Field field : fields) {
            //noinspection unchecked
            field.setValue(entity, lookup.getValue(field));
        }
        return entity;
    }

    @Override
    public TEntity clone(TEntity entity) {
        TEntity newEntity = newInstance();
        copy(entity, newEntity);
        return newEntity;
    }

    @Override
    public void copy(TEntity from, TEntity to) {
        for (Field field : fields) {
            //noinspection unchecked
            field.setValue(to, field.getValue(from));
        }
    }

    @Override
    public void entityToMap(TEntity entity, FieldValueMap<TEntity> map) {
        if (!keyField.metaInfo().isAutoIncremented() && keyField.getValue(entity) == null) {
            keyField.setValue(entity, keyField.metaInfo().generateValue());
        }

        for (Field field : fields) {
            //noinspection unchecked
            map.putValue(field, field.getValue(entity));
        }
    }

    @Override
    public ValueField<TEntity, TKey> getKeyField() {
        return keyField;
    }

    @Override
    public TKey getKey(TEntity entity) {
        return getKeyField().getValue(entity);
    }

    @Override
    public void setKey(TEntity entity, TKey key) {
        getKeyField().setValue(entity, key);
    }

    @SuppressWarnings("unchecked")
    private AbstractEntityType<TKey, TEntity> addFields(Field<TEntity, ?>... fields) {
        for (Field<TEntity, ?> field : fields) {
            if (field instanceof Bindable) {
                ((Bindable)field).bind(this);
            }
            this.fields.add(field);
            if (field instanceof RelationalField) {
                //noinspection unchecked
                relationalFields.add((RelationalField<TEntity, ?>)field);
            }
        }
        return this;
    }
}
