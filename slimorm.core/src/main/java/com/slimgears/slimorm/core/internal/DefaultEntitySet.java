// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.slimgears.slimorm.core.interfaces.queries.DeleteQuery;
import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntitySet;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.queries.Query;
import com.slimgears.slimorm.core.interfaces.RepositorySession;
import com.slimgears.slimorm.core.interfaces.queries.UpdateQuery;
import com.slimgears.slimorm.core.internal.interfaces.EntityCache;
import com.slimgears.slimorm.core.internal.interfaces.EntityStateTracker;
import com.slimgears.slimorm.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimorm.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimorm.core.internal.query.EntityDeleteQuery;
import com.slimgears.slimorm.core.internal.query.EntitySelectQuery;
import com.slimgears.slimorm.core.internal.query.EntityUpdateQuery;
import com.slimgears.slimorm.core.internal.query.QueryProvider;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class DefaultEntitySet<TKey, TEntity extends Entity<TKey>> implements EntitySet<TKey, TEntity>,
        RepositorySession.OnSaveChangesListener,
        RepositorySession.OnDiscardChangesListener {
    private final SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider;
    private final EntityType<TKey, TEntity> entityType;
    private EntityCache<TKey, TEntity> entityCache;
    private EntityStateTracker<TKey, TEntity> stateTracker;
    private QueryProvider<TKey, TEntity> queryProvider;

    public static class Provider<TKey, TEntity extends Entity<TKey>> implements EntitySet.Provider<TKey, TEntity> {
        private EntitySet<TKey, TEntity> entitySet = null;
        private final SessionServiceProvider sessionServiceProvider;
        private final EntityType<TKey, TEntity> entityType;
        private final Object syncRoot = new Object();

        public Provider(SessionServiceProvider sessionServiceProvider, EntityType<TKey, TEntity> entityType) {
            this.sessionServiceProvider = sessionServiceProvider;
            this.entityType = entityType;
        }

        public EntitySet<TKey, TEntity> get() {
            if (entitySet != null) return entitySet;
            synchronized (syncRoot) {
                if (entitySet != null) return entitySet;
                return entitySet = new DefaultEntitySet<>(sessionServiceProvider.getEntityServiceProvider(entityType), entityType);
            }
        }
    }

    private DefaultEntitySet(SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider, EntityType<TKey, TEntity> entityType) {
        this.sessionEntityServiceProvider = sessionEntityServiceProvider;
        this.entityType = entityType;
    }

    @Override
    public Query.Builder<TEntity> query() {
        return new EntitySelectQuery<>(entityType, getQueryProvider());
    }

    @Override
    public DeleteQuery.Builder<TEntity> deleteQuery() {
        return new EntityDeleteQuery<>(entityType, getQueryProvider());
    }

    @Override
    public UpdateQuery.Builder<TEntity> updateQuery() {
        return new EntityUpdateQuery<>(entityType, getQueryProvider());
    }

    @Override
    public TEntity addNew() {
        return add(entityType.newInstance());
    }

    @Override
    public TEntity add(TEntity entity) {
        getCache().put(entity);
        getStateTracker().entityAdded(entity);
        return entity;
    }

    @Override
    public void remove(TEntity entity) {
        getCache().invalidate(entity.getEntityId());
        getStateTracker().entityDeleted(entity);
    }

    @Override
    public void onSavingChanges(RepositorySession session) throws IOException {
        EntityStateTracker<TKey, TEntity> tracker = getStateTracker();
        insert(tracker.getAddedEntities());
        delete(tracker.getDeletedEntities());
        update(tracker.getModifiedEntities());
    }

    @Override
    public void onDiscardingChanges(RepositorySession session) {
        EntityStateTracker<TKey, TEntity> tracker = getStateTracker();
        EntityCache<TKey, TEntity> cache = getCache();
        for (TEntity entity : Iterables.concat(
                tracker.getModifiedEntities(),
                tracker.getAddedEntities(),
                tracker.getModifiedEntities())) {
            cache.invalidate(entity.getEntityId());
        }
        tracker.discardChanges();
    }

    private void insert(Collection<TEntity> entities) throws IOException {
        if (!entities.isEmpty()) {
            getQueryProvider()
                    .prepareInsert(entities)
                    .execute();
        }
    }

    private void delete(Collection<TEntity> entities) throws IOException {
        deleteQuery()
                .where(entityType.getKeyField().in(entitiesToIds(entities)))
                .prepare()
                .execute();
    }

    private void update(Collection<TEntity> entities) throws IOException {
        for (TEntity entity : entities) {
            updateQuery()
                    .where(entityType.getKeyField().equal(entity.getEntityId()))
                    .setAll(entity)
                    .prepare()
                    .execute();
        }
    }

    private Collection<TKey> entitiesToIds(Collection<TEntity> entities) {
        return transform(entities, new Function<TEntity, TKey>() {
            @Override
            public TKey apply(TEntity input) {
                return input.getEntityId();
            }
        });
    }

    private QueryProvider<TKey, TEntity> getQueryProvider() {
        if (queryProvider != null) return queryProvider;
        return queryProvider = sessionEntityServiceProvider.getQueryProvider();
    }

    private EntityCache<TKey, TEntity> getCache() {
        if (entityCache != null) return entityCache;
        return entityCache = sessionEntityServiceProvider.getEntityCache();
    }

    private EntityStateTracker<TKey, TEntity> getStateTracker() {
        if (stateTracker != null) return stateTracker;
        return stateTracker = sessionEntityServiceProvider.getEntityStateTracker();
    }
}
