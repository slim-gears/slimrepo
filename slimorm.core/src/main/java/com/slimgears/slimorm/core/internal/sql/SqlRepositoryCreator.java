// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql;

import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryCreator;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryModel;

import java.io.IOException;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public class SqlRepositoryCreator implements RepositoryCreator {
    private final SqlCommandExecutor sqlExecutor;
    private final SqlStatementBuilder sqlBuilder;

    public SqlRepositoryCreator(SqlSessionServiceProvider sessionServiceProvider) {
        this.sqlBuilder = sessionServiceProvider.getStatementBuilder();
        this.sqlExecutor = sessionServiceProvider.getExecutor();
    }

    @Override
    public void createRepository(RepositoryModel model) throws IOException {
        for (EntityType<?, ?> entityType : model.getEntityTypes()) {
            createEntityType(entityType);
        }
    }

    private void createEntityType(final EntityType<?, ?> entityType) throws IOException {
        SqlLazyCommand command = new SqlLazyCommand(sqlBuilder, new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.createTableStatement(entityType);
            }
        });
        sqlExecutor.execute(command);
    }
}
