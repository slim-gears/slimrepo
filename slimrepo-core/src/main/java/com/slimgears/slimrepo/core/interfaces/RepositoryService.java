// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces;

/**
 * Created by Denis on 02-Apr-15
 *
 */
public interface RepositoryService<TRepository extends Repository> {
    interface UpdateAction<TRepository extends Repository> {
        void execute(TRepository repository) throws Exception;
    }

    interface QueryAction<TRepository extends Repository, R> {
        R execute(TRepository repository) throws Exception;
    }

    TRepository open();
    void update(UpdateAction<TRepository> updateAction) throws Exception;
    <R> R query(QueryAction<TRepository, R> queryAction) throws Exception;
}
