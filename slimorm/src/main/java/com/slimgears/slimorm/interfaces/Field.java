// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

import java.util.Collection;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface Field<TEntity, T> {
    String getName();
    Class<T> getType();

    Predicate<TEntity> equal(T value);
    Predicate<TEntity> notEqual(T value);
    Predicate<TEntity> in(T... values);
    Predicate<TEntity> in(Collection<T> values);
    Predicate<TEntity> notIn(T... values);
    Predicate<TEntity> notIn(Collection<T> values);
    Predicate<TEntity> isNull();
    Predicate<TEntity> isNotNull();
}

