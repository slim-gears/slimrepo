// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface Repository<TSession extends RepositorySession> {
    interface UpdateAction<TSession extends RepositorySession> {
        void execute(TSession session) throws IOException;
    }

    interface QueryAction<TSession extends RepositorySession, R> {
        R execute(TSession session) throws IOException;
    }

    TSession open();
    void update(UpdateAction<TSession> updateAction) throws IOException;
    <R> R query(QueryAction<TSession, R> queryAction) throws IOException;
}
