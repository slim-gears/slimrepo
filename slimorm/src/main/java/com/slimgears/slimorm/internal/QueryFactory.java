// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.DeleteQuery;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Query;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.UpdateQuery;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface QueryFactory {
    <TKey, TEntity extends Entity<TKey>> Query<TEntity> createQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>> DeleteQuery<TEntity> createDeleteQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>> UpdateQuery<TEntity> createUpdateQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType);
}
