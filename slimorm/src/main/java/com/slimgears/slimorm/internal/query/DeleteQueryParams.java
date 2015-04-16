// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.query;

import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.predicates.Predicate;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class DeleteQueryParams<TKey, TEntity extends Entity<TKey>> extends ConditionalQueryParams<TKey, TEntity, DeleteQueryParams<TKey, TEntity>> {
    public DeleteQueryParams(EntityType<TKey, TEntity> entityType, Predicate<TEntity> predicate, QueryPagination pagination) {
        super(entityType, predicate, pagination);
    }

    @Override
    public DeleteQueryParams<TKey, TEntity> fork() {
        return new DeleteQueryParams<>(entityType, predicate, clonePagination());
    }
}
