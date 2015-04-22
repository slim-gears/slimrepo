// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.queries.DeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.Query;
import com.slimgears.slimrepo.core.interfaces.queries.UpdateQuery;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface EntitySet<TKey, TEntity extends Entity<TKey>> {
    interface Provider<TKey, TEntity extends Entity<TKey>> {
        EntitySet<TKey, TEntity> get();
    }

    Query.Builder<TEntity> query();
    DeleteQuery.Builder<TEntity> deleteQuery();
    UpdateQuery.Builder<TEntity> updateQuery();

    TEntity[] toArray() throws IOException;
    List<TEntity> toList() throws IOException;
    Map<TKey, TEntity> toMap();

    TEntity addNew();
    TEntity add(TEntity entity);
    void add(Iterable<TEntity> entities);
    void remove(TEntity entity);
}
