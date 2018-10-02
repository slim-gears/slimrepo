// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 02-Apr-15
 *
 */
public interface EntitySet<TEntity> {
    interface Provider<TEntity> {
        EntitySet<TEntity> get();
    }

    interface Transformer<TEntity, T> {
        T transform(TEntity entity);
    }

    EntitySelectQuery.Builder<TEntity> query();
    EntityDeleteQuery.Builder<TEntity> deleteQuery();
    EntityUpdateQuery.Builder<TEntity> updateQuery();

    long countAll() throws Exception;
    long countAllWhere(Condition<TEntity> predicate) throws Exception;

    <T> TEntity findFirst(ValueField<TEntity, T> field, T value) throws Exception;
    TEntity findFirstWhere(Condition<TEntity> predicate) throws Exception;
    <T> TEntity[] findAllBy(ValueField<TEntity, T> field, T value) throws Exception;
    TEntity[] findAllWhere(Condition<TEntity> predicate) throws Exception;

    TEntity[] toArray() throws Exception;
    List<TEntity> toList() throws Exception;
    <T> Map<T, TEntity> toMap(Field<TEntity, T> keyField) throws Exception;
    <K, V> Map<K, V> toMap(Field<TEntity, K> keyField, Field<TEntity, V> valueField) throws Exception;
    <T> Collection<T> map(Transformer<TEntity, T> mapper) throws Exception;

    @SuppressWarnings("unchecked")
    TEntity[] add(TEntity... entities) throws Exception;
    TEntity add(TEntity entity) throws Exception;
    void addAll(Iterable<TEntity> entities) throws Exception;
    void mergeAll(Iterable<TEntity> entities) throws Exception;
    void remove(TEntity entity) throws Exception;
    void removeAll(Iterable<TEntity> entities) throws Exception;
}
