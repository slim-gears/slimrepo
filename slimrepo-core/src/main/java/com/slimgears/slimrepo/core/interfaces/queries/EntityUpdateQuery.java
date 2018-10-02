// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.queries;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 07-Apr-15
 *
 */
public interface EntityUpdateQuery {
    interface Builder<T> extends QueryBuilder<T, EntityUpdateQuery, Builder<T>> {
        <V> Builder<T> set(Field<T, V> field, V value);
        Builder<T> setAll(T entity);
        <V> Builder<T> exclude(Field<T, V> field);
    }

    void execute() throws Exception;
}
