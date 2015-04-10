// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.internal.AbstractUpdateQuery;
import com.slimgears.slimorm.internal.EntityCache;

import java.io.IOException;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public class SqlUpdateQuery<TKey, TEntity extends Entity<TKey>> extends AbstractUpdateQuery<TKey, TEntity> {
    private final SqlCommand command;
    protected final SqlCommandExecutor executor;

    class CommandBuilder implements SqlLazyCommand.CommandBuilder {
        @Override
        public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
            return sqlBuilder.buildUpdateStatement(
                    new SqlStatementBuilder.UpdateParameters()
                        .setEntityType(elementType)
                        .setPredicate(predicate)
                        .setCommandParameters(parameters)
                        .setUpdateFields(updateFields));
        }
    }

    public SqlUpdateQuery(SqlRepositorySession session, EntityCache<TKey, TEntity> cache, final EntityType<TKey, TEntity> elementType, SqlStatementBuilder sqlBuilder) {
        super(cache, elementType);
        executor = session.getExecutor();
        command = new SqlLazyCommand(sqlBuilder, new CommandBuilder());
    }

    @Override
    public void execute() throws IOException {
        executor.execute(command);
    }
}
