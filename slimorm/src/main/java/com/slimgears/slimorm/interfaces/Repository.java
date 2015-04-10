// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface Repository<TSession extends RepositorySession> {
    interface UpdateAction<TSession extends RepositorySession> {
        void execute(TSession connection) throws IOException;
    }

    interface QueryAction<TSession extends RepositorySession, R> {
        R execute(TSession connection) throws IOException;
    }

    TSession open();
    void update(UpdateAction<TSession> updateAction) throws IOException;
    <R> R query(QueryAction<TSession, R> queryAction) throws IOException;
}
