// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity extends Entity<?>, T> {

    interface MetaInfo<TEntity extends Entity<?>, T> {
        EntityType<?, TEntity> getEntityType();
        String getName();
        Class<T> getValueType();
        boolean isNullable();
    }

    MetaInfo<TEntity, T> metaInfo();
    Condition<TEntity> isNull();
    Condition<TEntity> isNotNull();
}

