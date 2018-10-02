// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.sqlite;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSchemeProvider;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class JdbcSessionServiceProvider extends AbstractSqlSessionServiceProvider {
    private final Connection connection;
    private final Closeable closer;

    public JdbcSessionServiceProvider(SqlOrmServiceProvider serviceProvider, Connection connection, Closeable closer) {
        super(serviceProvider);
        this.connection = connection;
        this.closer = closer;
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
    public void close() throws IOException {
        if (closer != null) closer.close();
    }
}
