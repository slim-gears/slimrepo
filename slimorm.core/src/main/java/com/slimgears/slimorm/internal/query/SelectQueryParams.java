// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.query;

import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.predicates.Predicate;
import com.slimgears.slimorm.internal.OrderFieldInfo;

import java.util.Collection;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class SelectQueryParams<TKey, TEntity extends Entity<TKey>> extends ConditionalQueryParams<TKey, TEntity, SelectQueryParams<TKey, TEntity>> {
    public Collection<OrderFieldInfo> order;

    public SelectQueryParams(EntityType<TKey, TEntity> entityType, Predicate<TEntity> predicate, Collection<OrderFieldInfo> order, QueryPagination pagination) {
        super(entityType, predicate, pagination);
        this.order = order;
    }

    @Override
    public SelectQueryParams<TKey, TEntity> fork() {
        return new SelectQueryParams<>(
                entityType,
                predicate,
                cloneCollection(order),
                clonePagination());
    }
}
