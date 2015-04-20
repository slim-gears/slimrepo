// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;

import java.io.IOException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractRepositoryService<TRepository extends Repository> implements RepositoryService<TRepository> {
    private final OrmServiceProvider ormServiceProvider;
    private final RepositoryModel repositoryModel;

    protected AbstractRepositoryService(OrmServiceProvider ormServiceProvider, RepositoryModel repositoryModel) {
        this.ormServiceProvider = ormServiceProvider;
        this.repositoryModel = repositoryModel;
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
}
