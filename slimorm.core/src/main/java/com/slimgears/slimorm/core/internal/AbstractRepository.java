// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal;

import com.slimgears.slimorm.core.interfaces.Repository;
import com.slimgears.slimorm.core.interfaces.RepositorySession;
import com.slimgears.slimorm.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimorm.core.internal.interfaces.SessionServiceProvider;

import java.io.IOException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractRepository<TSession extends RepositorySession> implements Repository<TSession> {
    private final OrmServiceProvider ormServiceProvider;
    private final RepositoryModel repositoryModel;

    protected AbstractRepository(OrmServiceProvider ormServiceProvider, RepositoryModel repositoryModel) {
        this.ormServiceProvider = ormServiceProvider;
        this.repositoryModel = repositoryModel;
    }

    @Override
    public void update(UpdateAction<TSession> action) throws IOException {
        try (TSession session = open()) {
            action.execute(session);
            session.saveChanges();
        }
    }

    @Override
    public <TResult> TResult query(QueryAction<TSession, TResult> action) throws IOException {
        try (TSession session = open()) {
            return action.execute(session);
        }
    }

    @Override
    public TSession open() {
        return createSession(createSessionServiceProvider(repositoryModel));
    }

    protected SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        return ormServiceProvider.createSessionServiceProvider(model);
    }

    protected abstract TSession createSession(SessionServiceProvider sessionServiceProvider);
}
