// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.predicates;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface CompositePredicate<TEntity> extends Predicate<TEntity> {
    Predicate<TEntity>[] getArguments();
}
