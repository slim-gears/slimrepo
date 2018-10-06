// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.interfaces.*;
import com.slimgears.slimrepo.core.internal.query.DefaultEntityDeleteQuery;
import com.slimgears.slimrepo.core.internal.query.DefaultEntitySelectQuery;
import com.slimgears.slimrepo.core.internal.query.DefaultEntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;

import java.util.Collection;
import java.util.Collections;


/**
 * Created by Denis on 05-Apr-15
 *
 */
public class DefaultEntitySet<TKey, TEntity> extends AbstractEntitySet<TKey, TEntity> implements
        RepositorySessionNotifier.Listener {
    protected final SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider;
    private EntityCache<TKey, TEntity> entityCache;
    private EntityStateTracker<TEntity> stateTracker;
    private QueryProvider<TKey, TEntity> queryProvider;

    public static class Provider<TKey, TEntity> implements EntitySet.Provider<TEntity> {
        private EntitySet<TEntity> entitySet = null;
        private final SessionServiceProvider sessionServiceProvider;
        private final EntityType<TKey, TEntity> entityType;
        private final Object syncRoot = new Object();

        public Provider(SessionServiceProvider sessionServiceProvider, EntityType<TKey, TEntity> entityType) {
            this.sessionServiceProvider = sessionServiceProvider;
            this.entityType = entityType;
        }

        public EntitySet<TEntity> get() {
            if (entitySet != null) return entitySet;
            synchronized (syncRoot) {
                if (entitySet != null) return entitySet;
                DefaultEntitySet<TKey, TEntity> instance = new DefaultEntitySet<>(sessionServiceProvider.getEntityServiceProvider(entityType), entityType);
                sessionServiceProvider.getEntitySessionNotifier().addListener(entityType, instance);
                return entitySet = instance;
            }
        }
    }

    public DefaultEntitySet(SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider, EntityType<TKey, TEntity> entityType) {
        super(entityType);
        this.sessionEntityServiceProvider = sessionEntityServiceProvider;
    }

    @Override
    public EntitySelectQuery.Builder<TEntity> query() {
        return new DefaultEntitySelectQuery<>(entityType, getQueryProvider(), getCache());
    }

    @Override
    public EntityDeleteQuery.Builder<TEntity> deleteQuery() {
        return new DefaultEntityDeleteQuery<>(entityType, getQueryProvider());
    }

    @Override
    public EntityUpdateQuery.Builder<TEntity> updateQuery() {
        return new DefaultEntityUpdateQuery<>(entityType, getQueryProvider());
    }

    @Override
    public void addAll(Iterable<TEntity> entities) throws Exception {
        for (TEntity entity : entities) {
            getStateTracker().entityAdded(entity);
        }
    }

    @Override
    public void removeAll(Iterable<TEntity> entities) throws Exception {
        for (TEntity entity : entities) {
            getCache().invalidate(entityType.getKey(entity));
            getStateTracker().entityDeleted(entity);
        }
    }

    @Override
    public void onSavingChanges(Repository session) throws Exception {
        EntityStateTracker<TEntity> tracker = getStateTracker();
        insert(tracker.getAddedEntities());
        delete(tracker.getDeletedEntities());
        update(tracker.getModifiedEntities());
        tracker.clearChanges();
    }

    @Override
    public void onDiscardingChanges(Repository session) {
        EntityStateTracker<TEntity> tracker = getStateTracker();
        EntityCache<TKey, TEntity> cache = getCache();

        //noinspection unchecked
        Stream
                .of(tracker.getModifiedEntities(),
                    tracker.getAddedEntities(),
                    tracker.getModifiedEntities())
                .flatMap(Stream::of)
                .map(entityType::getKey)
                .forEach(cache::invalidate);

        tracker.clearChanges();
    }

    @Override
    public void onClosing(Repository session) {
    }

    private void insert(Collection<TEntity> entities) throws Exception {
        for (TEntity entity : entities) {
            insertEntity(entity);
        }
    }

    private void insertEntity(TEntity entity) throws Exception {
        CloseableIterator<TKey> keysIterator = getQueryProvider()
                .prepareInsert(Collections.singletonList(entity))
                .execute();
        try {
            if (keysIterator.hasNext()) {
                TKey key = keysIterator.next();
                if (key.getClass() == entityType.getKeyField().metaInfo().getValueType()) {
                    entityType.setKey(entity, key);
                }
            }
        } finally {
            keysIterator.close();
        }
        getCache().put(entity);
    }

    private void delete(Collection<TEntity> entities) throws Exception {
        if (entities.isEmpty()) return;
        deleteQuery()
                .where(entityType.getKeyField().in(Stream.of(entities)
                                .map(entityType::getKey)
                                .collect(Collectors.toList())))
                .prepare()
                .execute();
    }

    private void update(Collection<TEntity> entities) throws Exception {
        if (entities.isEmpty()) return;
        for (TEntity entity : entities) {
            updateQuery()
                    .where(entityType.getKeyField().eq(entityType.getKey(entity)))
                    .setAll(entity)
                    .prepare()
                    .execute();
        }
    }

    protected QueryProvider<TKey, TEntity> getQueryProvider() {
        if (queryProvider != null) return queryProvider;
        return queryProvider = sessionEntityServiceProvider.getQueryProvider();
    }

    protected EntityCache<TKey, TEntity> getCache() {
        if (entityCache != null) return entityCache;
        return entityCache = sessionEntityServiceProvider.getEntityCache();
    }

    protected EntityStateTracker<TEntity> getStateTracker() {
        if (stateTracker != null) return stateTracker;
        return stateTracker = sessionEntityServiceProvider.getEntityStateTracker();
    }
}
