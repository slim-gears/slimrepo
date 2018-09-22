// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.conditions;

/**
 * Created by Denis on 11-Apr-15
 *
 */
public interface CompositeCondition<TEntity> extends Condition<TEntity> {
    Condition<TEntity>[] getArguments();
}
