// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.interfaces;

import com.slimgears.slimorm.core.interfaces.entities.Entity;

import java.util.concurrent.Callable;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface EntityCache<TKey, TEntity extends Entity<TKey>> {
    TEntity get(TKey id, Callable<TEntity> valueLoader);
    TEntity getIfPresent(TKey id);
    void put(TEntity entity);
    void invalidateAll();
    void invalidateAll(TKey... keys);
    void invalidate(TKey id);
}
