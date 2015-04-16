// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.internal.AbstractSessionServiceProvider;
import com.slimgears.slimorm.internal.interfaces.RepositoryCreator;
import com.slimgears.slimorm.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimorm.internal.interfaces.TransactionProvider;

import java.io.IOException;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlSessionServiceProvider extends AbstractSessionServiceProvider implements SqlSessionServiceProvider {
    private final SqlStatementBuilder sqlBuilder;
    private SqlCommandExecutor sqlExecutor;
    private TransactionProvider transactionProvider;

    public AbstractSqlSessionServiceProvider(SqlOrmServiceProvider serviceProvider) {
        this.sqlBuilder = serviceProvider.getStatementBuilder();
    }

    protected boolean isOpen() {
        return sqlExecutor != null;
    }

    protected void open() {
        sqlExecutor = createCommandExecutor();
    }

    @Override
    public void close() throws IOException {
        if (isOpen()) {
            sqlExecutor.close();
            sqlExecutor = null;
        }
    }

    protected abstract SqlCommandExecutor createCommandExecutor();
    protected abstract TransactionProvider createTransactionProvider();

    @Override
    protected <TKey, TEntity extends Entity<TKey>> SessionEntityServiceProvider<TKey, TEntity> createEntityServiceProvider(EntityType<TKey, TEntity> entityType) {
        return new SqlSessionEntityServiceProvider<>(this, entityType);
    }

    @Override
    public SqlCommandExecutor getExecutor() {
        return sqlExecutor != null
                ? sqlExecutor
                : (sqlExecutor = createCommandExecutor());
    }

    @Override
    public SqlStatementBuilder getStatementBuilder() {
        return sqlBuilder;
    }

    @Override
    public TransactionProvider getTransactionProvider() {
        return transactionProvider != null
                ? transactionProvider
                : (transactionProvider = createTransactionProvider());
    }

    @Override
    public RepositoryCreator createRepositoryCreator() {
        return new SqlRepositoryCreator(this);
    }
}
