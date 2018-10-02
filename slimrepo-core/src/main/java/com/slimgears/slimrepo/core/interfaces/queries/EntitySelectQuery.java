// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.queries;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

import java.util.Map;

/**
 * Created by Denis on 02-Apr-15
 *
 */
public interface EntitySelectQuery<T> extends SelectQuery<T> {
    interface Builder<T> extends QueryBuilder<T, EntitySelectQuery<T>, Builder<T>> {
        Builder<T> orderAsc(Field<T, ?>... fields);
        Builder<T> orderDesc(Field<T, ?>... fields);
        <S> SelectQuery<S> select(Field<T, S> field);
        <K, V> Map<K, V> selectToMap(Field<T, K> keyField, Field<T, V> valueField) throws Exception;
    }

    <K> Map<K, T> toMap(Field<T, K> keyField) throws Exception;
}
