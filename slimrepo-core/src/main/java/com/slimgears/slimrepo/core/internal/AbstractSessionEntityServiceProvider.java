// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;
import com.slimgears.slimrepo.core.internal.interfaces.EntityStateTracker;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public abstract class AbstractSessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>> implements SessionEntityServiceProvider<TKey, TEntity> {
    private EntityCache<TKey, TEntity> entityCache;
    private EntityStateTracker<TKey, TEntity> stateTracker;

    @Override
    public EntityCache<TKey, TEntity> getEntityCache() {
        return entityCache != null
                ? entityCache
                : (entityCache = new HashMapEntityCache<>());
    }

    @Override
    public EntityStateTracker<TKey, TEntity> getEntityStateTracker() {
        return stateTracker != null
                ? stateTracker
                : (stateTracker = new HashSetEntityStateTracker<>());
    }
}
