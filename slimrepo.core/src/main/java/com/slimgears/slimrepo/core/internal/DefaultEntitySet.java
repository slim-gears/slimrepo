// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.slimgears.slimrepo.core.interfaces.queries.DeleteQuery;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.queries.Query;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.queries.UpdateQuery;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;
import com.slimgears.slimrepo.core.internal.interfaces.EntityStateTracker;
import com.slimgears.slimrepo.core.internal.interfaces.RepositorySessionNotifier;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.query.EntityDeleteQuery;
import com.slimgears.slimrepo.core.internal.query.EntitySelectQuery;
import com.slimgears.slimrepo.core.internal.query.EntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class DefaultEntitySet<TKey, TEntity extends Entity<TKey>> implements EntitySet<TKey, TEntity>,
        RepositorySessionNotifier.Listener {
    private final SessionEntityServiceProvider<TKey, TEntity> sessionEntityServiceProvider;
    private final SessionServiceProvider sessionServiceProvider;
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
                return entitySet = new DefaultEntitySet<>(sessionServiceProvider, entityType);
            }
        }
    }

    private DefaultEntitySet(SessionServiceProvider sessionServiceProvider, EntityType<TKey, TEntity> entityType) {
        this.sessionServiceProvider = sessionServiceProvider;
        this.sessionEntityServiceProvider = sessionServiceProvider.getEntityServiceProvider(entityType);
        this.entityType = entityType;

        sessionServiceProvider.addListener(this);
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
    public TEntity[] toArray() throws IOException {
        return query().prepare().toArray();
    }

    @Override
    public List<TEntity> toList() throws IOException {
        return query().prepare().toList();
    }

    @Override
    public Map<TKey, TEntity> toMap() {
        Map<TKey, TEntity> map = new HashMap<>();
        for (Iterator<TEntity> it = query().prepare().iterator(); it.hasNext(); ) {
            TEntity entity = it.next();
            map.put(entity.getEntityId(), entity);
        }
        return map;
    }

    @Override
    public TEntity addNew() {
        return add(entityType.newInstance());
    }

    @Override
    public TEntity add(TEntity entity) {
        getStateTracker().entityAdded(entity);
        return entity;
    }

    @Override
    public void add(Iterable<TEntity> entities) {
        for (TEntity entity : entities) {
            add(entity);
        }
    }

    @Override
    public void remove(TEntity entity) {
        getCache().invalidate(entity.getEntityId());
        getStateTracker().entityDeleted(entity);
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
