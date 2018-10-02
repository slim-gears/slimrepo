// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.sqlite;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            Connection connection = DriverManager.getConnection(url);
            return new JdbcSessionServiceProvider(this, connection, () -> {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
