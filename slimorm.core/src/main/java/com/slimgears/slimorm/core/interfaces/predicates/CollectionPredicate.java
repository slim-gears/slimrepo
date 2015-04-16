// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.predicates;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface CollectionPredicate<TEntity, T> extends FieldPredicate<TEntity, T> {
    T[] getValues();
}
