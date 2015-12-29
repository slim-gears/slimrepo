// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import java.util.Collection;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public interface EntityStateTracker<TEntity> {
    Collection<TEntity> getModifiedEntities();
    Collection<TEntity> getAddedEntities();
    Collection<TEntity> getDeletedEntities();

    void entityModified(TEntity entity);
    void entityDeleted(TEntity entity);
    void entityAdded(TEntity entity);

    void clearChanges();
    boolean hasChanges();
}
