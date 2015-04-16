// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.predicates.Predicate;
import com.slimgears.slimorm.core.internal.UpdateFieldInfo;

import java.util.Collection;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class UpdateQueryParams<TKey, TEntity extends Entity<TKey>> extends ConditionalQueryParams<TKey, TEntity, UpdateQueryParams<TKey, TEntity>> {
    public Collection<UpdateFieldInfo> updates;

    public UpdateQueryParams(EntityType<TKey, TEntity> entityType, Predicate<TEntity> predicate, Collection<UpdateFieldInfo> updates, QueryPagination pagination) {
        super(entityType, predicate, pagination);
        this.updates = updates;
    }

    @Override
    public UpdateQueryParams<TKey, TEntity> fork() {
        return new UpdateQueryParams<>(
                entityType,
                predicate,
                cloneCollection(updates),
                clonePagination());
    }
}
