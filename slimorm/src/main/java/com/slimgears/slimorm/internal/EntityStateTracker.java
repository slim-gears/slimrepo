// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Entity;

import java.util.Set;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public interface EntityStateTracker<TKey, TEntity extends Entity<TKey>> {
    Set<TEntity> getModifiedEntities();
    Set<TEntity> getAddedEntities();
    Set<TEntity> getDeletedEntities();

    void entityModified(TEntity entity);
    void entityDeleted(TEntity entity);
    void entityAdded(TEntity entity);

    void discardChanges();
    boolean hasChanges();
}
