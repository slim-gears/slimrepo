// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.BinaryPredicate;
import com.slimgears.slimorm.interfaces.predicates.TernaryPredicate;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface NumberField<TEntity, T> extends ValueField<TEntity, T> {
    BinaryPredicate<TEntity, T> greaterThan(T value);
    BinaryPredicate<TEntity, T> lessThan(T value);
    BinaryPredicate<TEntity, T> greaterOrEqual(T value);
    BinaryPredicate<TEntity, T> lessOrEqual(T value);
    TernaryPredicate<TEntity, T> between(T min, T max);
}
