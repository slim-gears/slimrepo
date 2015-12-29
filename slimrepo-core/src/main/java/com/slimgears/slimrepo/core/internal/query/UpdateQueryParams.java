// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.internal.UpdateFieldInfo;

import java.util.Collection;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class UpdateQueryParams<TKey, TEntity> extends ConditionalQueryParams<TKey, TEntity, UpdateQueryParams<TKey, TEntity>> {
    public Collection<UpdateFieldInfo> updates;

    public UpdateQueryParams(EntityType<TKey, TEntity> entityType, Condition<TEntity> condition, Collection<UpdateFieldInfo> updates, QueryPagination pagination) {
        super(entityType, condition, pagination);
        this.updates = updates;
    }

    @Override
    public UpdateQueryParams<TKey, TEntity> fork() {
        return new UpdateQueryParams<>(
                entityType,
                condition,
                cloneCollection(updates),
                clonePagination());
    }
}
