// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Predicate;
import com.slimgears.slimorm.interfaces.Query;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public abstract class AbstractQueryBase<TKey, TEntity extends Entity<TKey>, TQuery> {
    protected Predicate predicate = null;
    protected EntityCache<TKey, TEntity> cache;
    protected final EntityType<TKey, TEntity> elementType;
    protected int limitEntries = Integer.MAX_VALUE;
    protected int skipEntries = 0;

    protected abstract TQuery self();

    protected AbstractQueryBase(EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> elementType) {
        this.cache = cache;
        this.elementType = elementType;
    }

    protected void addPredicate(Predicate predicate) {
        this.predicate = (this.predicate != null)
                ? this.predicate.and(predicate)
                : predicate;
    }


    public TQuery where(Predicate predicate) {
        addPredicate(predicate);
        return self();
    }

    public TQuery skip(int number) {
        this.skipEntries = number;
        return self();
    }

    public TQuery limit(int number) {
        this.limitEntries = number;
        return self();
    }

}
