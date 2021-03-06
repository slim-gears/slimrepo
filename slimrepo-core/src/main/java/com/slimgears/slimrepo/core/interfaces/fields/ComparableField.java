// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

/**
 * Created by Denis on 06-Apr-15
 *
 */
public interface ComparableField<TEntity, T> extends ValueField<TEntity, T> {
    Condition<TEntity> greaterThan(T value);
    Condition<TEntity> lessThan(T value);
    Condition<TEntity> greaterOrEq(T value);
    Condition<TEntity> lessOrEq(T value);
    Condition<TEntity> between(T min, T max);
}
