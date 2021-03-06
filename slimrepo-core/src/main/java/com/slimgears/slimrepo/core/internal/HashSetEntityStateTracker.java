// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.internal.interfaces.EntityStateTracker;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public class HashSetEntityStateTracker<TEntity> implements EntityStateTracker<TEntity> {
    private final Set<TEntity> modifiedEntities = new LinkedHashSet<>();
    private final Set<TEntity> addedEntities = new LinkedHashSet<>();
    private final Set<TEntity> deletedEntities = new LinkedHashSet<>();

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
    public void clearChanges() {
        addedEntities.clear();
        deletedEntities.clear();
        modifiedEntities.clear();
    }

    @Override
    public boolean hasChanges() {
        return  (!modifiedEntities.isEmpty()) ||
                (!addedEntities.isEmpty()) ||
                (!deletedEntities.isEmpty());
    }
}
