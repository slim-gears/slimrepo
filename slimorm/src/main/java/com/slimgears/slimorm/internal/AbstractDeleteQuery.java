// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.DeleteQuery;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public abstract class AbstractDeleteQuery<TKey, TEntity extends Entity<TKey>> extends AbstractQueryBase<TKey, TEntity, DeleteQuery<TEntity>> implements DeleteQuery<TEntity> {
    protected AbstractDeleteQuery(EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> elementType) {
        super(cache, elementType);
    }

    @Override
    protected DeleteQuery<TEntity> self() {
        return this;
    }
}
