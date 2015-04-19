// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;

import java.util.ArrayList;
import java.util.Collection;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public abstract class AbstractQueryParams<TKey, TEntity extends Entity<TKey>, TQueryParams extends AbstractQueryParams<TKey, TEntity, TQueryParams>> {
    public final EntityType<TKey, TEntity> entityType;

    public AbstractQueryParams(EntityType<TKey, TEntity> entityType) {
        this.entityType = entityType;
    }

    public abstract TQueryParams fork();

    protected <T> Collection<T> cloneCollection(Collection<T> collection) {
        return collection != null ? new ArrayList<>(collection) : null;
    }
}
