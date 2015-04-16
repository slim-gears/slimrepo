// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.internal.interfaces.EntityStateTracker;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class HashSetEntityStateTracker<TKey, TEntity extends Entity<TKey>> implements EntityStateTracker<TKey, TEntity> {
    private final Set<TEntity> modifiedEntities = Collections.newSetFromMap(new ConcurrentHashMap<TEntity, Boolean>());
    private final Set<TEntity> addedEntities = Collections.newSetFromMap(new ConcurrentHashMap<TEntity, Boolean>());
    private final Set<TEntity> deletedEntities = Collections.newSetFromMap(new ConcurrentHashMap<TEntity, Boolean>());

    @Override
    public Set<TEntity> getModifiedEntities() {
        return modifiedEntities;
    }

    @Override
    public Set<TEntity> getAddedEntities() {
        return addedEntities;
    }

    @Override
    public Set<TEntity> getDeletedEntities() {
        return deletedEntities;
    }

    @Override
    public void entityModified(TEntity entity) {
        if (!addedEntities.contains(entity) || !deletedEntities.contains(entity)) modifiedEntities.add(entity);
    }

    @Override
    public void entityDeleted(TEntity entity) {
        if (addedEntities.contains(entity)) {
            addedEntities.remove(entity);
        } else {
            deletedEntities.add(entity);
            modifiedEntities.remove(entity);
        }
    }

    @Override
    public void entityAdded(TEntity entity) {
        if (deletedEntities.contains(entity)) {
            deletedEntities.remove(entity);
        } else {
            addedEntities.add(entity);
            modifiedEntities.remove(entity);
        }
    }

    @Override
    public void discardChanges() {
        addedEntities.clear();
        deletedEntities.clear();
        modifiedEntities.clear();
    }

    @Override
    public boolean hasChanges() {
        return  (modifiedEntities != null && !modifiedEntities.isEmpty()) ||
                (addedEntities != null && !addedEntities.isEmpty()) ||
                (deletedEntities != null && !deletedEntities.isEmpty());
    }
}
