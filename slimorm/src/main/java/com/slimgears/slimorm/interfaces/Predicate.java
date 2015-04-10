// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface Predicate<TEntity> {
    interface Factory<TEntity> {
        <T> Predicate<TEntity> equals(String name, T value);
        <T> Predicate<TEntity> greaterThan(String name, T value);
        <T> Predicate<TEntity> lessThan(String name, T value);
        Predicate<TEntity> startsWith(String name, String substr);
        Predicate<TEntity> endsWith(String name, String substr);
        Predicate<TEntity> contains(String name, String substr);
    }

    interface Builder<TEntity> {
        Predicate<TEntity> build(Factory factory);
    }

    Predicate<TEntity> and(Predicate other);
    Predicate<TEntity> or(Predicate other);
}
