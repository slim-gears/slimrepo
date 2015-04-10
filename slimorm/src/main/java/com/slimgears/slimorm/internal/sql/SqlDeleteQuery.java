// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.internal.AbstractDeleteQuery;
import com.slimgears.slimorm.internal.EntityCache;

import java.io.IOException;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public class SqlDeleteQuery<TKey, TEntity extends Entity<TKey>> extends AbstractDeleteQuery<TKey, TEntity> {
    private final SqlCommand command;
    private final SqlCommandExecutor executor;

    class CommandBuilder implements SqlLazyCommand.CommandBuilder {
        @Override
        public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
            return sqlBuilder.buildDeleteStatement(
                    new SqlStatementBuilder.DeleteParameters()
                    .setEntityType(elementType)
                    .setCommandParameters(parameters)
                    .setPredicate(predicate)
                    .setLimit(limitEntries)
                    .setOffset(skipEntries));
        }
    }

    public SqlDeleteQuery(SqlRepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType, SqlStatementBuilder sqlBuilder) {
        super(cache, entityType);
        executor = session.getExecutor();
        command = new SqlLazyCommand(sqlBuilder, new CommandBuilder());
    }

    @Override
    public void execute() throws IOException {
        executor.execute(command);
    }
}
