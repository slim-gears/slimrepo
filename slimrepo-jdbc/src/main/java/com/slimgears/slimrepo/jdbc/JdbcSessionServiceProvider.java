// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSchemeProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class JdbcSessionServiceProvider extends AbstractSqlSessionServiceProvider {
    private final static Logger log = Logger.getLogger(JdbcSessionServiceProvider.class.toString());
    private final Connection connection;

    public JdbcSessionServiceProvider(SqlOrmServiceProvider serviceProvider, String url) {
        super(serviceProvider);
        this.connection = JdbcHelper.execute(() -> DriverManager.getConnection(url));
        log.log(Level.INFO, "Connection open");
    }

    @Override
    protected SqlCommandExecutor createCommandExecutor() {
        return new JdbcCommandExecutor(connection, this);
    }

    @Override
    protected TransactionProvider createTransactionProvider() {
        return new JdbcTransactionProvider(connection);
    }

    @Override
    protected <TKey, TEntity> SessionEntityServiceProvider<TKey, TEntity> createEntityServiceProvider(EntityType<TKey, TEntity> entityType) {
        return new SqlSessionEntityServiceProvider<>(this, entityType);
    }

    @Override
    protected SqlSchemeProvider createSchemeProvider() {
        return new JdbcSchemeProvider(getOrmServiceProvider().getSyntaxProvider(), connection);
    }

    @Override
    public void close() {
        try {
            connection.close();
            log.log(Level.INFO, "Connection closed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
