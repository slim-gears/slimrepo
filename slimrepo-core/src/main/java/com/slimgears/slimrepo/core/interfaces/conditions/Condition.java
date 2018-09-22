// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.conditions;

/**
 * Created by Denis on 02-Apr-15
 *
 */
public interface Condition<TEntity> {
    PredicateType getType();
    Condition<TEntity> and(Condition<TEntity> other);
    Condition<TEntity> or(Condition<TEntity> other);
}
