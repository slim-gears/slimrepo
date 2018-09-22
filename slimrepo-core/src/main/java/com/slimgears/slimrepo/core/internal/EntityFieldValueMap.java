// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public class EntityFieldValueMap<TKey, TEntity> implements FieldValueMap<TEntity> {
    private final Map<String, Object> valueMap = new HashMap<>();

    public EntityFieldValueMap(EntityType<TKey, TEntity> entityType, TEntity entity) {
        entityType.entityToMap(entity, this);
    }

    @Override
    public <T> T getValue(Field<TEntity, T> field) {
        //noinspection unchecked
        return (T)valueMap.get(field.metaInfo().getName());
    }

    @Override
    public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
        valueMap.put(field.metaInfo().getName(), value);
        return this;
    }
}
