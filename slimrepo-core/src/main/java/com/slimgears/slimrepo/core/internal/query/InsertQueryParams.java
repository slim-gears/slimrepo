// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.util.Collection;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public class InsertQueryParams<TKey, TEntity> extends AbstractQueryParams<TKey, TEntity, InsertQueryParams<TKey, TEntity>> {
    public final Collection<TEntity> entities;

    public InsertQueryParams(EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) {
        super(entityType);
        this.entities = entities;
    }

    @Override
    public InsertQueryParams<TKey, TEntity> fork() {
        return new InsertQueryParams<>(entityType, cloneCollection(entities));
    }
}
