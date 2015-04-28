// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryCreator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public class SqlRepositoryCreator implements RepositoryCreator {
    private final TransactionProvider transactionProvider;
    private final SqlCommandExecutor sqlExecutor;
    private final SqlStatementBuilder sqlBuilder;

    public SqlRepositoryCreator(SqlSessionServiceProvider sessionServiceProvider) {
        this.transactionProvider = sessionServiceProvider.getTransactionProvider();
        this.sqlBuilder = sessionServiceProvider.getOrmServiceProvider().getStatementBuilder();
        this.sqlExecutor = sessionServiceProvider.getExecutor();
    }

    @Override
    public void createRepository(RepositoryModel model) throws IOException {
        transactionProvider.beginTransaction();
        try {
            Set<EntityType> createdEntityTypes = new HashSet<>();
            for (EntityType<?, ?> entityType : model.getEntityTypes()) {
                createEntityType(createdEntityTypes, entityType);
            }
        } catch (Throwable e) {
            transactionProvider.cancelTransaction();
            throw e;
        }
        transactionProvider.commitTransaction();
    }

    private void createEntityType(Set<EntityType> createdEntityTypes, final EntityType<?, ?> entityType) throws IOException {
        if (!createdEntityTypes.add(entityType)) return;

        for (RelationalField field : entityType.getRelationalFields()) {
            createEntityType(createdEntityTypes, field.metaInfo().getRelatedEntityType());
        }

        SqlLazyCommand command = new SqlLazyCommand(sqlBuilder, new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.createTableStatement(entityType);
            }
        });
        sqlExecutor.execute(command);
    }
}
