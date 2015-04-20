// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryCreator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositorySessionNotifier;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public abstract class AbstractSessionServiceProvider implements SessionServiceProvider {
    private final List<RepositorySessionNotifier.Listener> sessionListeners = new ArrayList<>();
    private RepositoryCreator repositoryCreator;

    private final LoadingCache<EntityType, SessionEntityServiceProvider> entityServiceProviderCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<EntityType, SessionEntityServiceProvider>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public SessionEntityServiceProvider load(EntityType key) throws Exception {
                    return createEntityServiceProvider(key);
                }
            });

    private final LoadingCache<EntityType, EntitySet.Provider> entitySetProviderCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<EntityType, EntitySet.Provider>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public EntitySet.Provider load(EntityType key) throws Exception {
                    return createEntitySetProvider(key);
                }
            });

    protected abstract <TKey, TEntity extends Entity<TKey>> SessionEntityServiceProvider<TKey, TEntity> createEntityServiceProvider(EntityType<TKey, TEntity> entityType);
    protected abstract RepositoryCreator createRepositoryCreator();

    protected <TKey, TEntity extends Entity<TKey>> EntitySet.Provider<TKey, TEntity> createEntitySetProvider(EntityType<TKey, TEntity> entityType) {
        return new DefaultEntitySet.Provider<>(this, entityType);
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> SessionEntityServiceProvider<TKey, TEntity> getEntityServiceProvider(EntityType<TKey, TEntity> entityType) {
        try {
            //noinspection unchecked
            return entityServiceProviderCache.get(entityType);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> EntitySet.Provider<TKey, TEntity> getEntitySetProvider(EntityType<TKey, TEntity> entityType) {
        try {
            //noinspection unchecked
            return entitySetProviderCache.get(entityType);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RepositoryCreator getRepositoryCreator() {
        return repositoryCreator != null
                ? repositoryCreator
                : (repositoryCreator = createRepositoryCreator());
    }

    @Override
    public void addListener(RepositorySessionNotifier.Listener listener) {
        sessionListeners.add(listener);
    }

    @Override
    public void removeListener(RepositorySessionNotifier.Listener listener) {
        sessionListeners.remove(listener);
    }

    @Override
    public void onSavingChanges(Repository session) throws IOException {
        for (RepositorySessionNotifier.Listener listener : sessionListeners) {
            listener.onSavingChanges(session);
        }
    }

    @Override
    public void onDiscardingChanges(Repository session) {
        for (RepositorySessionNotifier.Listener listener : sessionListeners) {
            listener.onDiscardingChanges(session);
        }
    }

    @Override
    public void onClosing(Repository session) {
        for (RepositorySessionNotifier.Listener listener : sessionListeners) {
            listener.onClosing(session);
        }
    }
}
