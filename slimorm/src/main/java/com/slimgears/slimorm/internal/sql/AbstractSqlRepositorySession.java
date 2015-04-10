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
    private SqlCommandExecutor commandExecutor;
    private final SqlCommandExecutorFactory commandExecutorFactory;
    private final SqlStatementBuilder statementBuilder;
    private final Repository repository;

    public AbstractSqlRepositorySession(SqlCommandExecutorFactory commandExecutorFactory, SqlStatementBuilder statementBuilder, Repository repository) {
        this.commandExecutorFactory = commandExecutorFactory;
        this.statementBuilder = statementBuilder;
        this.repository = repository;
    }

    @Override
    public SqlCommandExecutor getExecutor() {
        if (commandExecutor != null) return commandExecutor;
        return commandExecutor = commandExecutorFactory.createCommandExecutor(repository, this);
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
    public void close() throws IOException {
        if (commandExecutor != null) {
            commandExecutor.close();
            commandExecutor = null;
        }
    }
}
