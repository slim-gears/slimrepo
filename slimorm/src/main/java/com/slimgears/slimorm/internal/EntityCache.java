// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Entity;

import java.util.Set;
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
