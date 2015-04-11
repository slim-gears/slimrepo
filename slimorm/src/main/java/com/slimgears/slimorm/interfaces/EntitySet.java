// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface EntitySet<TKey, TEntity extends Entity<TKey>> {
    Query<TEntity> query();
    DeleteQuery<TEntity> deleteQuery();
    UpdateQuery<TEntity> updateQuery();

    TEntity addNew();
    TEntity add(TEntity entity);
    void remove(TEntity entity);
}
