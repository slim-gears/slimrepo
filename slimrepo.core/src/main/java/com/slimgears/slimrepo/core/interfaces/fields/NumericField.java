// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface NumericField<TEntity, T> extends ValueField<TEntity, T> {
    Condition<TEntity> greaterThan(T value);
    Condition<TEntity> lessThan(T value);
    Condition<TEntity> greaterOrEqual(T value);
    Condition<TEntity> lessOrEqual(T value);
    Condition<TEntity> between(T min, T max);
}
