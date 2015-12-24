// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractRepositoryService<TRepository extends Repository> implements RepositoryService<TRepository> {
    LoadingCache<EntityType, AutoEntitySet> sessionEntityServiceProviderCache = CacheBuilder.newBuilder()
            .build(new CacheLoader<EntityType, AutoEntitySet>() {
                @Override
                public AutoEntitySet load(EntityType entityType) throws Exception {
                    //noinspection unchecked
                    return new AutoEntitySet(ormServiceProvider, entityType, repositoryModel, AbstractRepositoryService.this);
                }
            });

    private final OrmServiceProvider ormServiceProvider;
    private final RepositoryModel repositoryModel;

    protected AbstractRepositoryService(OrmServiceProvider ormServiceProvider, RepositoryModel repositoryModel, FieldTypeMappingInstaller... typeMapperInstallers) {
        this.ormServiceProvider = ormServiceProvider;
        this.repositoryModel = repositoryModel;

        if (typeMapperInstallers.length > 0) {
            FieldTypeMappingRegistrar registrar = ormServiceProvider.getFieldTypeMapperRegistrar();
            for (FieldTypeMappingInstaller installer : typeMapperInstallers) {
                installer.install(registrar);
            }
        }
    }

    @Override
    public void update(UpdateAction<TRepository> action) throws IOException {
        try (TRepository repo = open()) {
            action.execute(repo);
            repo.saveChanges();
        }
    }

    @Override
    public <TResult> TResult query(QueryAction<TRepository, TResult> action) throws IOException {
        try (TRepository repo = open()) {
            return action.execute(repo);
        }
    }

    @Override
    public TRepository open() {
        return createRepository(createSessionServiceProvider(repositoryModel));
    }

    protected SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        return ormServiceProvider.createSessionServiceProvider(model);
    }

    protected abstract TRepository createRepository(SessionServiceProvider sessionServiceProvider);

    protected <TKey, TEntity> EntitySet<TEntity> getEntitySet(EntityType<TKey, TEntity> entityType) {
        try {
            //noinspection unchecked
            return sessionEntityServiceProviderCache.get(entityType);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
