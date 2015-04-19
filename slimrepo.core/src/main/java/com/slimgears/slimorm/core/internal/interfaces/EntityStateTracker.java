// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.interfaces;

import com.slimgears.slimorm.core.interfaces.entities.Entity;

import java.util.Collection;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public interface EntityStateTracker<TKey, TEntity extends Entity<TKey>> {
    Collection<TEntity> getModifiedEntities();
    Collection<TEntity> getAddedEntities();
    Collection<TEntity> getDeletedEntities();

    void entityModified(TEntity entity);
    void entityDeleted(TEntity entity);
    void entityAdded(TEntity entity);

    void clearChanges();
    boolean hasChanges();
}
