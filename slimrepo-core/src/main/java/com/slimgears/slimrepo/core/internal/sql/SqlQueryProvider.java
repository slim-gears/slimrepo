// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.query.*;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;
import com.slimgears.slimrepo.core.utilities.Lazy;

import java.util.Collection;

/**
 * Created by Denis on 13-Apr-15
 *
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

    interface QueryExecutor<T> {
        T execute(SqlCommandExecutor executor, SqlCommand command) throws Exception;
    }

    private <T> PreparedQuery<T> prepare(Function<SqlStatementBuilder, SqlCommand> supplier, QueryExecutor<T> querySupplier) {
        Supplier<SqlCommand> lazyCommand = Lazy.of(() -> supplier.apply(getBuilder()));
        return () -> querySupplier.execute(getExecutor(), lazyCommand.get());
    }

    @Override
    public PreparedQuery<CloseableIterator<TKey>> prepareInsert(final Collection<TEntity> entities) {
        return prepare(builder -> builder.insertStatement(new InsertQueryParams<>(entityType, entities)), SqlCommandExecutor::insert);
    }

    @Override
    public PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        return prepare(builder -> builder.selectStatement(query), SqlCommandExecutor::select);
    }

    @Override
    public PreparedQuery<Long> prepareCount(final SelectQueryParams<TKey, TEntity> query) {
        return prepare(builder -> builder.countStatement(query), SqlCommandExecutor::count);
    }

    @Override
    public PreparedQuery<Void> prepareUpdate(final UpdateQueryParams<TKey, TEntity> query) {
        return prepare(
                builder -> builder.updateStatement(query),
                (executor, command) -> { executor.execute(command); return null; });
    }

    @Override
    public PreparedQuery<Void> prepareDelete(final DeleteQueryParams<TKey, TEntity> query) {
        return prepare(
                builder -> builder.deleteStatement(query),
                (executor, command) -> { executor.execute(command); return null; });
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
