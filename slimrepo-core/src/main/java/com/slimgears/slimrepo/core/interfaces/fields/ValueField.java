// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

import java.util.Collection;

/**
 * Created by Denis on 11-Apr-15
 *
 */
public interface ValueField<TEntity, T> extends Field<TEntity, T> {
    Condition<TEntity> eq(T value);
    Condition<TEntity> notEq(T value);
    @SuppressWarnings("unchecked")
    Condition<TEntity> in(T... values);
    Condition<TEntity> in(Collection<T> values);
    @SuppressWarnings("unchecked")
    Condition<TEntity> notIn(T... values);
    Condition<TEntity> notIn(Collection<T> values);
}
