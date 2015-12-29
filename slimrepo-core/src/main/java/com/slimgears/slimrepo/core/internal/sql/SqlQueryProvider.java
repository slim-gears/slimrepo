// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

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

import java.util.Collection;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public class SqlQueryProvider<TKey, TEntity> implements QueryProvider<TKey, TEntity> {
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
        final SqlCommand command = new SqlLazyCommand(getBuilder(), (sqlBuilder1, parameters) ->
                sqlBuilder1.insertStatement(new InsertQueryParams<>(entityType, entities), parameters));
        return () -> {
            getExecutor().execute(command.getStatement(), command.getParameters().getValues());
            return null;
        };
    }

    @Override
    public PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), (sqlBuilder1, parameters) ->
                sqlBuilder1.selectStatement(query, parameters));
        return () -> getExecutor().select(command.getStatement(), command.getParameters().getValues());
    }

    @Override
    public PreparedQuery<Long> prepareCount(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), (sqlBuilder1, parameters) ->
                sqlBuilder1.countStatement(query, parameters));
        return () -> getExecutor().count(command.getStatement(), command.getParameters().getValues());
    }

    @Override
    public PreparedQuery<Void> prepareUpdate(final UpdateQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), (sqlBuilder1, parameters) -> sqlBuilder1.updateStatement(query, parameters));
        return () -> {
            getExecutor().execute(command.getStatement(), command.getParameters().getValues());
            return null;
        };
    }

    @Override
    public PreparedQuery<Void> prepareDelete(final DeleteQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), (sqlBuilder1, parameters) ->
                sqlBuilder1.deleteStatement(query, parameters));
        return () -> {
            getExecutor().execute(command.getStatement(), command.getParameters().getValues());
            return null;
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
