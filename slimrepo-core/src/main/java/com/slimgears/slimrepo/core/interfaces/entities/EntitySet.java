// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
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

    long countAll() throws IOException;
    long countAllWhere(Condition<TEntity> predicate) throws IOException;

    <T> TEntity findFirst(ValueField<TEntity, T> field, T value) throws IOException;
    TEntity findFirstWhere(Condition<TEntity> predicate) throws IOException;
    <T> TEntity[] findAllBy(ValueField<TEntity, T> field, T value) throws IOException;
    TEntity[] findAllWhere(Condition<TEntity> predicate) throws IOException;

    TEntity[] toArray() throws IOException;
    List<TEntity> toList() throws IOException;
    <T> Map<T, TEntity> toMap(Field<TEntity, T> keyField) throws IOException;
    <K, V> Map<K, V> toMap(Field<TEntity, K> keyField, Field<TEntity, V> valueField) throws IOException;
    <T> Collection<T> map(Transformer<TEntity, T> mapper) throws IOException;

    @SuppressWarnings("unchecked")
    TEntity[] add(TEntity... entities) throws IOException;
    TEntity add(TEntity entity) throws IOException;
    void addAll(Iterable<TEntity> entities) throws IOException;
    void mergeAll(Iterable<TEntity> entities) throws IOException;
    void remove(TEntity entity) throws IOException;
    void removeAll(Iterable<TEntity> entities) throws IOException;
}
