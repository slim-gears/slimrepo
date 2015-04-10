// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface NumberField<TEntity, T> extends Field<TEntity, T> {
    Predicate<TEntity> greaterThan(T value);
    Predicate<TEntity> lessThan(T value);
    Predicate<TEntity> between(T min, T max);
    Predicate<TEntity> greaterOrEqual(T value);
    Predicate<TEntity> lessOrEqual(T value);
}