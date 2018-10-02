// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class JdbcOrmServiceProvider extends AbstractSqliteOrmServiceProvider {
    private final String url;

    public JdbcOrmServiceProvider(String url) {
        this.url = url;
    }

    @Override
    public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        try {
            SessionServiceProvider sessionServiceProvider = new JdbcSessionServiceProvider(this, url);
            sessionServiceProvider.getRepositoryCreator().upgradeOrCreate(model);
            return sessionServiceProvider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
