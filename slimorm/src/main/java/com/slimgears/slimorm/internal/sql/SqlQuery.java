// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.AbstractQuery;
import com.slimgears.slimorm.internal.EntityCache;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class SqlQuery<TKey, TEntity extends Entity<TKey>> extends AbstractQuery<TKey, TEntity> {
    private final SqlCommand selectCommand;
    private final SqlCommand countCommand;
    private final SqlCommandExecutor executor;

    class EntityIterator implements Iterator<TEntity> {
        private final Iterator<FieldValueLookup<TEntity>> rowIterator;

        public EntityIterator(Iterator<FieldValueLookup<TEntity>> rowIterator) {
            this.rowIterator = rowIterator;
        }

        @Override
        public boolean hasNext() {
            return rowIterator.hasNext();
        }

        @Override
        public TEntity next() {
            final FieldValueLookup<TEntity> row = rowIterator.next();
            TKey id = row.getValue(elementType.getKeyField());
            return cache.get(id, new Callable<TEntity>() {
                @Override
                public TEntity call() throws Exception {
                    return elementType.newInstance(row);
                }
            });
        }
    }

    class SelectCommandBuilder implements SqlLazyCommand.CommandBuilder {
        @Override
        public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
            return sqlBuilder.buildSelectStatement(
                    new SqlStatementBuilder.SelectParameters()
                        .setEntityType(elementType)
                        .setPredicate(predicate)
                        .setCommandParameters(parameters)
                        .setOrderFields(orderFields)
                        .setLimit(limitEntries)
                        .setOffset(skipEntries));
        }
    }

    class CountCommandBuilder implements SqlLazyCommand.CommandBuilder {
        @Override
        public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
            return sqlBuilder.buildCountStatement(
                    new SqlStatementBuilder.CountParameters()
                        .setEntityType(elementType)
                        .setPredicate(predicate)
                        .setCommandParameters(parameters)
                        .setLimit(limitEntries)
                        .setOffset(skipEntries));
        }
    }

    public SqlQuery(SqlRepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> elementType, SqlStatementBuilder sqlBuilder) {
        super(cache, elementType);
        executor = session.getExecutor();
        selectCommand = new SqlLazyCommand(sqlBuilder, new SelectCommandBuilder());
        countCommand = new SqlLazyCommand(sqlBuilder, new CountCommandBuilder());
    }

    @Override
    protected Iterator<TEntity> execute() throws IOException {
        Iterable<FieldValueLookup<TEntity>> rows = executor.select(selectCommand);
        return new EntityIterator(rows.iterator());
    }

    @Override
    protected int executeCount() throws IOException {
        return executor.count(countCommand);
    }
}
