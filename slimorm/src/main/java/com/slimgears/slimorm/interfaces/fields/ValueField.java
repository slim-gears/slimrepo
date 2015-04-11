// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.BinaryPredicate;
import com.slimgears.slimorm.interfaces.predicates.CollectionPredicate;
import com.slimgears.slimorm.interfaces.predicates.UnaryPredicate;

import java.util.Collection;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface ValueField<TEntity, T> extends Field<TEntity, T> {
    BinaryPredicate<TEntity, T> equal(T value);
    BinaryPredicate<TEntity, T> notEqual(T value);
    CollectionPredicate<TEntity, T> in(T... values);
    CollectionPredicate<TEntity, T> in(Collection<T> values);
    CollectionPredicate<TEntity, T> notIn(T... values);
    CollectionPredicate<TEntity, T> notIn(Collection<T> values);
}
