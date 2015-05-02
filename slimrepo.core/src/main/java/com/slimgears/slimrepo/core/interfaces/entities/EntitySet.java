// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface EntitySet<TEntity extends Entity<?>> {
    interface Provider<TEntity extends Entity<?>> {
        EntitySet<TEntity> get();
    }

    EntitySelectQuery.Builder<TEntity> query();
    EntityDeleteQuery.Builder<TEntity> deleteQuery();
    EntityUpdateQuery.Builder<TEntity> updateQuery();

    TEntity[] toArray() throws IOException;
    List<TEntity> toList() throws IOException;
    <T> Map<T, TEntity> toMap(Field<TEntity, T> keyField) throws IOException;
    <K, V> Map<K, V> toMap(Field<TEntity, K> keyField, Field<TEntity, V> valueField) throws IOException;

    @SuppressWarnings("unchecked")
    TEntity[] add(TEntity... entities) throws IOException;
    TEntity add(TEntity entity) throws IOException;
    void addAll(Iterable<TEntity> entities) throws IOException;
    void remove(TEntity entity) throws IOException;
    void removeAll(Iterable<TEntity> entities) throws IOException;
}
