// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.predicates.Predicate;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public abstract class ConditionalQueryParams<TKey, TEntity extends Entity<TKey>, TQueryParams extends ConditionalQueryParams<TKey, TEntity, TQueryParams>>
        extends AbstractQueryParams<TKey, TEntity, TQueryParams> {
    public Predicate<TEntity> predicate;
    public QueryPagination pagination;

    public ConditionalQueryParams(EntityType<TKey, TEntity> entityType, Predicate<TEntity> predicate, QueryPagination pagination) {
        super(entityType);
        this.predicate = predicate;
        this.pagination = pagination;
    }

    protected QueryPagination clonePagination() {
        return pagination != null ? pagination.fork() : null;
    }
}
