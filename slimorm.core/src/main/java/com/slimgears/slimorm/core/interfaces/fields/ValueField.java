// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.fields;

import com.slimgears.slimorm.core.interfaces.predicates.Predicate;

import java.util.Collection;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface ValueField<TEntity, T> extends Field<TEntity, T> {
    Predicate<TEntity> equal(T value);
    Predicate<TEntity> notEqual(T value);
    Predicate<TEntity> in(T... values);
    Predicate<TEntity> in(Collection<T> values);
    Predicate<TEntity> notIn(T... values);
    Predicate<TEntity> notIn(Collection<T> values);
}
