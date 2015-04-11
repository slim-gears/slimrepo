// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.UnaryPredicate;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity, T> {
    Class<TEntity> getEntityClass();
    String getName();
    Class<T> getType();
    UnaryPredicate<TEntity, T> isNull();
    UnaryPredicate<TEntity, T> isNotNull();
}

