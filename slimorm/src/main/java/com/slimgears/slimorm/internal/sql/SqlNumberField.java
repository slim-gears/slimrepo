// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.NumberField;
import com.slimgears.slimorm.interfaces.Predicate;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public class SqlNumberField<TEntity, T> extends AbstractSqlField<TEntity, T> implements NumberField<TEntity, T> {
    public SqlNumberField(String name, Class<T> type) {
        super(name, type);
    }

    @Override
    public Predicate<TEntity> greaterThan(T value) {
        return operator("> %1$s", value);
    }

    @Override
    public Predicate<TEntity> lessThan(T value) {
        return operator("< %1$s", value);
    }

    @Override
    public Predicate<TEntity> between(T min, T max) {
        return operator("BETWEEN %1$s AND %1$s", min, max);
    }

    @Override
    public Predicate<TEntity> greaterOrEqual(T value) {
        return operator(">= %1$s", value);
    }

    @Override
    public Predicate<TEntity> lessOrEqual(T value) {
        return operator("<= %1$s", value);
    }
}
