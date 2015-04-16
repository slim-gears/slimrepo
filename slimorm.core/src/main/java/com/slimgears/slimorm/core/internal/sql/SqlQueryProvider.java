// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimorm.core.internal.interfaces.EntityCache;
import com.slimgears.slimorm.core.internal.query.DeleteQueryParams;
import com.slimgears.slimorm.core.internal.query.InsertQueryParams;
import com.slimgears.slimorm.core.internal.query.PreparedQuery;
import com.slimgears.slimorm.core.internal.query.QueryProvider;
import com.slimgears.slimorm.core.internal.query.SelectQueryParams;
import com.slimgears.slimorm.core.internal.query.UpdateQueryParams;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public class SqlQueryProvider<TKey, TEntity extends Entity<TKey>> implements QueryProvider<TKey, TEntity> {
    private final EntityType<TKey, TEntity> entityType;
    private final SqlSessionServiceProvider serviceProvider;
    private SqlStatementBuilder sqlBuilder;
    private SqlCommandExecutor sqlExecutor;

    class EntityIterator implements CloseableIterator<TEntity> {
        private final EntityCache<TKey, TEntity> entityCache;
        private final CloseableIterator<FieldValueLookup<TEntity>> rowIterator;
        private final Field<TEntity, TKey> keyField;

        EntityIterator(CloseableIterator<FieldValueLookup<TEntity>> rowIterator) {
            this.entityCache = serviceProvider.getEntityServiceProvider(entityType).getEntityCache();
            this.rowIterator = rowIterator;
            this.keyField = entityType.getKeyField();
        }

        @Override
        public void close() throws IOException {
            rowIterator.close();
        }

        @Override
        public boolean hasNext() {
            return rowIterator.hasNext();
        }

        @Override
        public TEntity next() {
            final FieldValueLookup<TEntity> row = rowIterator.next();
            TKey id = row.getValue(keyField);
            return entityCache.get(id, new Callable<TEntity>() {
                @Override
                public TEntity call() throws Exception {
                    return entityType.newInstance(row);
                }
            });
        }
    }

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
    public PreparedQuery<CloseableIterator<TEntity>> prepareSelect(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.selectStatement(query, parameters);
            }
        });
        return new PreparedQuery<CloseableIterator<TEntity>>() {
            @Override
            public CloseableIterator<TEntity> execute() throws IOException {
                CloseableIterator<FieldValueLookup<TEntity>> rowIterator = getExecutor().select(command);
                return new EntityIterator(rowIterator);
            }
        };
    }

    @Override
    public PreparedQuery<Integer> prepareCount(final SelectQueryParams<TKey, TEntity> query) {
        final SqlCommand command = new SqlLazyCommand(getBuilder(), new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.countStatement(query, parameters);
            }
        });
        return new PreparedQuery<Integer>() {
            @Override
            public Integer execute() throws IOException {
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
        return sqlBuilder = serviceProvider.getStatementBuilder();
    }
}
