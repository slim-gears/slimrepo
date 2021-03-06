// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;

import java.util.Collection;

/**
 * Created by Denis on 07-Apr-15
 *
 */
public interface EntityType<TKey, TEntity> {
    Class<TEntity> getEntityClass();
    String getName();
    ValueField<TEntity, TKey> getKeyField();
    Collection<Field<TEntity, ?>> getFields();
    Collection<RelationalField<TEntity, ?>> getRelationalFields();
    TEntity clone(TEntity entity);
    void copy(TEntity from, TEntity to);
    TEntity newInstance();
    TEntity newInstance(FieldValueLookup<TEntity> lookup);
    void entityToMap(TEntity entity, FieldValueMap<TEntity> map);
    TKey getKey(TEntity entity);
    void setKey(TEntity entity, TKey key);

    interface Bindable {
        void bind(EntityType<?, ?> entityType);
    }
}
