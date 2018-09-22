// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import java.util.concurrent.Callable;

/**
 * Created by Denis on 07-Apr-15
 *
 */
@SuppressWarnings("unused")
public interface EntityCache<TKey, TEntity> {
    TEntity get(TKey id, Callable<TEntity> valueLoader);
    TEntity getIfPresent(TKey id);
    void put(TEntity entity);
    void invalidateAll();
    @SuppressWarnings("unchecked")
    void invalidateAll(TKey... keys);
    void invalidate(TKey id);
}
