// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.fields;

import com.slimgears.slimorm.core.interfaces.predicates.Predicate;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity, T> {
    Class<TEntity> getEntityClass();
    String getName();
    Class<T> getType();
    Predicate<TEntity> isNull();
    Predicate<TEntity> isNotNull();
}

