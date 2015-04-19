// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class HashMapEntityCache<TKey, TEntity extends Entity<TKey>> implements EntityCache<TKey, TEntity> {
    protected final ConcurrentMap<TKey, TEntity> entityCache = new ConcurrentHashMap<>();

    @Override
    public TEntity get(TKey id, Callable<TEntity> valueLoader) {
        TEntity entity = entityCache.get(id);
        if (entity == null) {
            try {
                TEntity tmpEntity = valueLoader.call();
                entity = entityCache.putIfAbsent(id, tmpEntity);
                if (entity == null) entity = tmpEntity;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    @Override
    public TEntity getIfPresent(TKey id) {
        return entityCache.get(id);
    }

    @Override
    public void put(TEntity entity) {
        entityCache.putIfAbsent(entity.getEntityId(), entity);
    }

    @Override
    public void invalidateAll() {
        entityCache.clear();
    }

    @SafeVarargs
    @Override
    public final void invalidateAll(TKey... keys) {
        for (TKey key : keys) {
            entityCache.remove(key);
        }
    }

    @Override
    public void invalidate(TKey id) {
        entityCache.remove(id);
    }
}
