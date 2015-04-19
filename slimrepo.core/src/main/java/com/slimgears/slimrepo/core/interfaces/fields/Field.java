// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity, T> {
    interface MetaInfo<TEntity, T> {
        Class<TEntity> getEntityClass();
        String getName();
        Class<T> getType();
        boolean isNullable();
    }
    MetaInfo<TEntity, T> metaInfo();
    Condition<TEntity> isNull();
    Condition<TEntity> isNotNull();
}

