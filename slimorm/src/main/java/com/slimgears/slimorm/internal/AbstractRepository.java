// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;

import java.io.IOException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractRepository<TSession extends RepositorySession> implements Repository<TSession> {
    @Override
    public void update(UpdateAction<TSession> action) throws IOException {
        try (TSession session = open()) {
            action.execute(session);
        }
    }

    @Override
    public <TResult> TResult query(QueryAction<TSession, TResult> action) throws IOException {
        try (TSession session = open()) {
            return action.execute(session);
        }
    }
}
