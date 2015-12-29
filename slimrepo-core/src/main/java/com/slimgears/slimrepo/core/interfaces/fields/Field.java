// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity, T> {

    interface MetaInfo<T> {
        EntityType<?, ?> getEntityType();
        String getName();
        Class<T> getValueType();
        boolean isNullable();
    }

    T getValue(TEntity entity);
    void setValue(TEntity entity, T value);

    MetaInfo<T> metaInfo();
    Condition<TEntity> isNull();
    Condition<TEntity> isNotNull();
}
