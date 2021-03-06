// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.internal.query.QueryProvider;

/**
 * Created by Denis on 14-Apr-15
 *
 */
public interface SessionEntityServiceProvider<TKey, TEntity>  {
    QueryProvider<TKey, TEntity> getQueryProvider();
    EntityCache<TKey, TEntity> getEntityCache();
    EntityStateTracker<TEntity> getEntityStateTracker();
}
