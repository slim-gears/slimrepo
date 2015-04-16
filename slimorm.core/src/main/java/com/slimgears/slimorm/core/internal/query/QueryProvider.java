// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.RepositorySession;
import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimorm.core.internal.interfaces.EntityCache;

import java.util.Collection;

/**
 * Created by Denis on 12-Apr-15
 * <File Description>
 */
public interface QueryProvider<TKey, TEntity extends Entity<TKey>> {
    interface Factory {
        <TKey, TEntity extends Entity<TKey>> QueryProvider<TKey, TEntity> createProvider(RepositorySession session, EntityCache<TKey, TEntity> entityCache);
    }

    PreparedQuery<CloseableIterator<TEntity>> prepareSelect(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Integer> prepareCount(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareUpdate(UpdateQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareDelete(DeleteQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareInsert(Collection<TEntity> entities);
}
