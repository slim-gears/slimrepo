// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.fields;

import com.slimgears.slimorm.core.interfaces.conditions.Condition;

import java.util.Collection;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface ValueField<TEntity, T> extends Field<TEntity, T> {
    Condition<TEntity> equal(T value);
    Condition<TEntity> notEqual(T value);
    Condition<TEntity> in(T... values);
    Condition<TEntity> in(Collection<T> values);
    Condition<TEntity> notIn(T... values);
    Condition<TEntity> notIn(Collection<T> values);
}
