// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.conditions.Condition;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public abstract class ConditionalQueryParams<TKey, TEntity extends Entity<TKey>, TQueryParams extends ConditionalQueryParams<TKey, TEntity, TQueryParams>>
        extends AbstractQueryParams<TKey, TEntity, TQueryParams> {
    public Condition<TEntity> condition;
    public QueryPagination pagination;

    public ConditionalQueryParams(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition, QueryPagination pagination) {
        super(entityType);
        this.condition = condition;
        this.pagination = pagination;
    }

    protected QueryPagination clonePagination() {
        return pagination != null ? pagination.fork() : null;
    }
}
