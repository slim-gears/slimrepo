// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.conditions.Condition;
import com.slimgears.slimorm.core.internal.OrderFieldInfo;

import java.util.Collection;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class SelectQueryParams<TKey, TEntity extends Entity<TKey>> extends ConditionalQueryParams<TKey, TEntity, SelectQueryParams<TKey, TEntity>> {
    public Collection<OrderFieldInfo> order;

    public SelectQueryParams(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition, Collection<OrderFieldInfo> order, QueryPagination pagination) {
        super(entityType, condition, pagination);
        this.order = order;
    }

    @Override
    public SelectQueryParams<TKey, TEntity> fork() {
        return new SelectQueryParams<>(
                entityType,
                condition,
                cloneCollection(order),
                clonePagination());
    }
}
