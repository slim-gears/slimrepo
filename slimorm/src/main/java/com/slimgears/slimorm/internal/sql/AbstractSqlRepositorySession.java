// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.internal.AbstractRepositorySession;

import java.io.IOException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class AbstractSqlRepositorySession extends AbstractRepositorySession implements com.slimgears.slimorm.internal.sql.SqlRepositorySession {
    private SqlCommandExecutor sqlExecutor;
    private final SqlCommandExecutorFactory sqlExecutorFactory;
    private final SqlStatementBuilder statementBuilder;
    private final Repository repository;

    public AbstractSqlRepositorySession(SqlCommandExecutorFactory sqlExecutorFactory, SqlStatementBuilder statementBuilder, Repository repository) {
        this.sqlExecutorFactory = sqlExecutorFactory;
        this.statementBuilder = statementBuilder;
        this.repository = repository;
    }

    @Override
    public SqlCommandExecutor getExecutor() {
        if (!isOpen()) open();
        return sqlExecutor;
    }

    @Override
    public SqlStatementBuilder getStatementBuilder() {
        return statementBuilder;
    }

    @Override
    protected void beginTransaction() throws IOException {
        getExecutor().beginTransaction();
    }

    @Override
    protected void commitTransaction() throws IOException {
        getExecutor().commitTransaction();
    }

    @Override
    protected void cancelTransaction() throws IOException {
        getExecutor().cancelTransaction();
    }

    protected boolean isOpen() {
        return sqlExecutor != null;
    }

    protected void open() {
        sqlExecutor = sqlExecutorFactory.createCommandExecutor(repository, this);
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            sqlExecutor.close();
        }
    }
}
