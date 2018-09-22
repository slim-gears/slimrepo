// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
* Created by Denis on 13-Apr-15
*
*/
public abstract class ConditionalQueryParams<TKey, TEntity, TQueryParams extends ConditionalQueryParams<TKey, TEntity, TQueryParams>>
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
