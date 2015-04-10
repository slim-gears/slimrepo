// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.google.common.base.Joiner;
import com.slimgears.slimorm.interfaces.Predicate;
import com.slimgears.slimorm.internal.AbstractField;

import java.util.Collection;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public class AbstractSqlField<TEntity, T> extends AbstractField<TEntity, T> {
    AbstractSqlField(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public Predicate<TEntity> equal(T value) {
        return operator("= %1$s", value);
    }

    @Override
    public Predicate<TEntity> notEqual(T value) {
        return operator("<> %1$s", value);
    }

    @SafeVarargs
    @Override
    public final Predicate<TEntity> in(T... values) {
        return operator("IN (" + args(1, values.length) + ")", values);
    }

    @Override
    public Predicate<TEntity> in(Collection<T> values) {
        return operator("IN (" + args(1, values.size()) + ")", values.toArray());
    }

    @SafeVarargs
    @Override
    public final Predicate<TEntity> notIn(T... values) {
        return operator("NOT IN (" + args(1, values.length) + ")", values);
    }

    @Override
    public Predicate<TEntity> notIn(Collection<T> values) {
        return operator("NOT IN (" + args(1, values.size()) + ")", values.toArray());
    }

    @Override
    public Predicate<TEntity> isNull() {
        return operator("IS NULL");
    }

    @Override
    public Predicate<TEntity> isNotNull() {
        return operator("IS NOT NULL");
    }

    protected Predicate<TEntity> operator(String expression, Object... args) {
        return new SqlPredicate<>(field() + " " + expression, args);
    }

    protected String args(int startIndex, int count) {
        Object[] indexes = new Object[count];
        for (int i = 0; i < count; ++i) indexes[i] = "%" + (startIndex + i);
        return Joiner.on(',').join(indexes);
    }

    protected String field() {
        return "[" + getName() + "]";
    }
}
