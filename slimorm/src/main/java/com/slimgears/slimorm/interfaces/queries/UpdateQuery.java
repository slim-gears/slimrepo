// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.queries;

import com.slimgears.slimorm.interfaces.fields.Field;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface UpdateQuery {
    interface Builder<T> extends QueryBuilder<T, UpdateQuery, Builder<T>> {
        <V> Builder<T> set(Field<T, V> field, V value);
        Builder<T> setAll(T entity);
        <V> Builder<T> exclude(Field<T, V> field);
    }

    void execute() throws IOException;
}
