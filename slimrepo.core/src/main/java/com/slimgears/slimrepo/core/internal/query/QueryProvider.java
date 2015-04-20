// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;

import java.util.Collection;

/**
 * Created by Denis on 12-Apr-15
 * <File Description>
 */
public interface QueryProvider<TKey, TEntity extends Entity<TKey>> {
    interface Factory {
        <TKey, TEntity extends Entity<TKey>> QueryProvider<TKey, TEntity> createProvider(Repository session, EntityCache<TKey, TEntity> entityCache);
    }

    PreparedQuery<CloseableIterator<TEntity>> prepareSelect(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Long> prepareCount(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareUpdate(UpdateQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareDelete(DeleteQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareInsert(Collection<TEntity> entities);
}
