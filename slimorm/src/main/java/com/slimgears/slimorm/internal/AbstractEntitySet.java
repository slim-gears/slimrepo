// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.google.common.collect.Iterables;
import com.slimgears.slimorm.interfaces.DeleteQuery;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntitySet;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Query;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.UpdateQuery;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public abstract class AbstractEntitySet<TKey, TEntity extends Entity<TKey>> implements EntitySet<TKey, TEntity>,
        RepositorySession.OnSaveChangesListener,
        RepositorySession.OnDiscardChangesListener {
    private final RepositorySession session;
    private final QueryFactory queryFactory;
    private final EntityType<TKey, TEntity> entityType;
    private final EntityCache<TKey, TEntity> entityCache = new HashMapEntityCache<>();
    private final EntityStateTracker<TKey, TEntity> stateTracker = new HashSetEntityStateTracker<>();

    protected AbstractEntitySet(RepositorySession session, QueryFactory queryFactory, EntityType<TKey, TEntity> entityType) {
        this.session = session;
        this.queryFactory = queryFactory;
        this.entityType = entityType;
    }

    @Override
    public Query<TEntity> query() {
        return queryFactory.createQuery(session, entityCache, entityType);
    }

    @Override
    public DeleteQuery<TEntity> deleteQuery() {
        return queryFactory.createDeleteQuery(session, entityCache, entityType);
    }

    @Override
    public UpdateQuery<TEntity> updateQuery() {
        return queryFactory.createUpdateQuery(session, entityCache, entityType);
    }

    @Override
    public TEntity addNew() {
        return add(entityType.newInstance());
    }

    @Override
    public TEntity add(TEntity entity) {
        entityCache.put(entity);
        stateTracker.entityAdded(entity);
        return entity;
    }

    @Override
    public void remove(TEntity entity) {
        entityCache.invalidate(entity.getEntityId());
        stateTracker.entityDeleted(entity);
    }

    @Override
    public void onSavingChanges(RepositorySession session) throws IOException {
        insert(session, entityType, stateTracker.getAddedEntities());
        delete(session, entityType, stateTracker.getDeletedEntities());
        update(session, entityType, stateTracker.getModifiedEntities());
    }

    @Override
    public void onDiscardingChanges(RepositorySession session) {
        for (TEntity entity : Iterables.concat(
                stateTracker.getModifiedEntities(),
                stateTracker.getAddedEntities(),
                stateTracker.getModifiedEntities())) {
            entityCache.invalidate(entity.getEntityId());
        }
        stateTracker.discardChanges();
    }

    protected abstract void insert(RepositorySession session, EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) throws IOException;
    protected abstract void delete(RepositorySession session, EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) throws IOException;
    protected abstract void update(RepositorySession session, EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) throws IOException;
}
