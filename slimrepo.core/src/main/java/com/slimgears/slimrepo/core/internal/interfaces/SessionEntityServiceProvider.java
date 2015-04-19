// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public interface SessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>>  {
    QueryProvider<TKey, TEntity> getQueryProvider();
    EntityCache<TKey, TEntity> getEntityCache();
    EntityStateTracker<TKey, TEntity> getEntityStateTracker();
}
