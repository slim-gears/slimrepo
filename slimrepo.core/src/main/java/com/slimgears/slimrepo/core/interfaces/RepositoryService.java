// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces;

import java.io.IOException;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface RepositoryService<TRepository extends Repository> {
    interface UpdateAction<TRepository extends Repository> {
        void execute(TRepository repository) throws IOException;
    }

    interface QueryAction<TRepository extends Repository, R> {
        R execute(TRepository repository) throws IOException;
    }

    TRepository open();
    void update(UpdateAction<TRepository> updateAction) throws IOException;
    <R> R query(QueryAction<TRepository, R> queryAction) throws IOException;
}
