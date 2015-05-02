// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.query.DeleteQueryParams;
import com.slimgears.slimrepo.core.internal.query.InsertQueryParams;
import com.slimgears.slimrepo.core.internal.query.PreparedQuery;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;
import com.slimgears.slimrepo.core.internal.query.SelectQueryParams;
import com.slimgears.slimrepo.core.internal.query.UpdateQueryParams;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public class SqlQueryProvider<TKey, TEntity extends Entity<TKey>> implements QueryProvider<TKey, TEntity> {
    protected final EntityType<TKey, TEntity> entityType;
    protected final SqlSessionServiceProvider serviceProvider;
    private SqlStatementBuilder sqlBuilder;
    private SqlCommandExecutor sqlExecutor;

    public SqlQueryProvider(SqlSessionServiceProvider serviceProvider, EntityType<TKey, TEntity> entityType) {
        this.entityType = entityType;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public PreparedQuery<Void> prepareInsert(final Collection<TEntity> entities) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.insertStatement(new InsertQueryParams<>(entityType, entities), parameters);
            }
        });
        return new PreparedQuery<Void>() {
            @Override
            public Void execute() throws IOException {
                getExecutor().execute(command);
                return null;
            }
        };
    }

    @Override
    public PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.selectStatement(query, parameters);
            }
        });
        return new PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>>() {
            @Override
            public CloseableIterator<FieldValueLookup<TEntity>> execute() throws IOException {
                return getExecutor().select(command);
            }
        };
    }

    @Override
    public PreparedQuery<Long> prepareCount(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.countStatement(query, parameters);
            }
        });
        return new PreparedQuery<Long>() {
            @Override
            public Long execute() throws IOException {
                return getExecutor().count(command);
            }
        };
    }

    @Override
    public PreparedQuery<Void> prepareUpdate(final UpdateQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.updateStatement(query, parameters);
            }
        });
        return new PreparedQuery<Void>() {
            @Override
            public Void execute() throws IOException {
                getExecutor().execute(command);
                return null;
            }
        };
    }

    @Override
    public PreparedQuery<Void> prepareDelete(final DeleteQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.deleteStatement(query, parameters);
            }
        });
        return new PreparedQuery<Void>() {
            @Override
            public Void execute() throws IOException {
                getExecutor().execute(command);
                return null;
            }
        };
    }

    private SqlCommandExecutor getExecutor() {
        if (sqlExecutor != null) return sqlExecutor;
        return sqlExecutor = serviceProvider.getExecutor();
    }

    private SqlStatementBuilder getBuilder() {
        if (sqlBuilder != null) return sqlBuilder;
        return sqlBuilder = serviceProvider.getOrmServiceProvider().getStatementBuilder();
    }
}
