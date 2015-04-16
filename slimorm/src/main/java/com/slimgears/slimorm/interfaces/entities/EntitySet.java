// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.entities;

import com.slimgears.slimorm.interfaces.queries.DeleteQuery;
import com.slimgears.slimorm.interfaces.queries.Query;
import com.slimgears.slimorm.interfaces.queries.UpdateQuery;

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

    TEntity addNew();
    TEntity add(TEntity entity);
    void remove(TEntity entity);
}
