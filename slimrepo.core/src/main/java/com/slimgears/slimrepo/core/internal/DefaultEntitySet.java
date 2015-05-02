// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntitySelectQuery;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;
import com.slimgears.slimrepo.core.internal.interfaces.EntityStateTracker;
import com.slimgears.slimrepo.core.internal.interfaces.RepositorySessionNotifier;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.query.DefaultEntityDeleteQuery;
import com.slimgears.slimrepo.core.internal.query.DefaultEntitySelectQuery;
import com.slimgears.slimrepo.core.internal.query.DefaultEntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class DefaultEntitySet<TKey, TEntity extends Entity<TKey>> implements EntitySet<TEntity>,
        RepositorySessionNotifier.Listener {
    protected final SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider;
    protected final EntityType<TKey, TEntity> entityType;
    private EntityCache<TKey, TEntity> entityCache;
    private EntityStateTracker<TKey, TEntity> stateTracker;
    private QueryProvider<TKey, TEntity> queryProvider;

    public static class Provider<TKey, TEntity extends Entity<TKey>> implements EntitySet.Provider<TEntity> {
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
                sessionServiceProvider.addListener(instance);
                return entitySet = instance;
            }
        }
    }

    public DefaultEntitySet(SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider, EntityType<TKey, TEntity> entityType) {
        this.sessionEntityServiceProvider = sessionEntityServiceProvider;
        this.entityType = entityType;
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
    public TEntity[] toArray() throws IOException {
        return query().prepare().toArray();
    }

    @Override
    public List<TEntity> toList() throws IOException {
        return query().prepare().toList();
    }

    @Override
    public <T> Map<T, TEntity> toMap(Field<TEntity, T> keyField) throws IOException {
        return query().prepare().toMap(keyField);
    }

    @Override
    public <K, V> Map<K, V> toMap(Field<TEntity, K> keyField, Field<TEntity, V> valueField) throws IOException {
        return query().selectToMap(keyField, valueField);
    }

    @Override
    @SafeVarargs
    public final TEntity[] add(TEntity... entities) throws IOException {
        addAll(Arrays.asList(entities));
        return entities;
    }

    @Override
    public final TEntity add(TEntity entity) throws IOException {
        addAll(Collections.singletonList(entity));
        return entity;
    }

    @Override
    public void addAll(Iterable<TEntity> entities) throws IOException {
        for (TEntity entity : entities) {
            getStateTracker().entityAdded(entity);
        }
    }

    @Override
    public void remove(TEntity entity) throws IOException {
        removeAll(Collections.singletonList(entity));
    }

    @Override
    public void removeAll(Iterable<TEntity> entities) throws IOException {
        for (TEntity entity : entities) {
            getCache().invalidate(entity.getEntityId());
            getStateTracker().entityDeleted(entity);
        }
    }

    @Override
    public void onSavingChanges(Repository session) throws IOException {
        EntityStateTracker<TKey, TEntity> tracker = getStateTracker();
        insert(tracker.getAddedEntities());
        delete(tracker.getDeletedEntities());
        update(tracker.getModifiedEntities());
        tracker.clearChanges();
    }

    @Override
    public void onDiscardingChanges(Repository session) {
        EntityStateTracker<TKey, TEntity> tracker = getStateTracker();
        EntityCache<TKey, TEntity> cache = getCache();
        for (TEntity entity : Iterables.concat(
                tracker.getModifiedEntities(),
                tracker.getAddedEntities(),
                tracker.getModifiedEntities())) {
            cache.invalidate(entity.getEntityId());
        }
        tracker.clearChanges();
    }

    @Override
    public void onClosing(Repository session) {
    }

    private void insert(Collection<TEntity> entities) throws IOException {
        if (entities.isEmpty()) return;
        getQueryProvider().prepareInsert(entities).execute();
    }

    private void delete(Collection<TEntity> entities) throws IOException {
        if (entities.isEmpty()) return;
        deleteQuery()
                .where(entityType.getKeyField().in(entitiesToIds(entities)))
                .prepare()
                .execute();
    }

    private void update(Collection<TEntity> entities) throws IOException {
        if (entities.isEmpty()) return;
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

    protected QueryProvider<TKey, TEntity> getQueryProvider() {
        if (queryProvider != null) return queryProvider;
        return queryProvider = sessionEntityServiceProvider.getQueryProvider();
    }

    protected EntityCache<TKey, TEntity> getCache() {
        if (entityCache != null) return entityCache;
        return entityCache = sessionEntityServiceProvider.getEntityCache();
    }

    protected EntityStateTracker<TKey, TEntity> getStateTracker() {
        if (stateTracker != null) return stateTracker;
        return stateTracker = sessionEntityServiceProvider.getEntityStateTracker();
    }
}
