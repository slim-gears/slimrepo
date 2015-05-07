// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class DeleteQueryParams<TKey, TEntity extends Entity<TKey>> extends ConditionalQueryParams<TKey, TEntity, DeleteQueryParams<TKey, TEntity>> {
    public DeleteQueryParams(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition, QueryPagination pagination) {
        super(entityType, condition, pagination);
    }

    @Override
    public DeleteQueryParams<TKey, TEntity> fork() {
        return new DeleteQueryParams<>(entityType, condition, clonePagination());
    }
}
