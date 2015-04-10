// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.google.common.base.Function;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.AbstractEntitySet;
import com.slimgears.slimorm.internal.EntityFieldValueMap;
import com.slimgears.slimorm.internal.QueryFactory;
import com.slimgears.slimorm.internal.sql.SqlCommand;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlLazyCommand;
import com.slimgears.slimorm.internal.sql.SqlRepositorySession;
import com.slimgears.slimorm.internal.sql.SqlStatementBuilder;

import java.io.IOException;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class SqlEntitySet<TKey, TEntity extends Entity<TKey>> extends AbstractEntitySet<TKey, TEntity> {
    private final static int MAX_BULK_SIZE = 100;
    private final SqlCommandExecutor executor;
    private final SqlStatementBuilder sqlBuilder;

    public SqlEntitySet(SqlRepositorySession session, QueryFactory queryFactory, EntityType<TKey, TEntity> entityType) {
        super(session, queryFactory, entityType);
        sqlBuilder = session.getStatementBuilder();
        executor = session.getExecutor();
    }

    @Override
    protected void insert(RepositorySession session, final EntityType<TKey, TEntity> entityType, final Collection<TEntity> entities) throws IOException {
        SqlCommand command = new SqlLazyCommand(sqlBuilder, new SqlLazyCommand.CommandBuilder() {
            @Override
            public String buildCommand(SqlStatementBuilder sqlBuilder, SqlCommand.Parameters parameters) {
                return sqlBuilder.buildInsertStatement(
                        new SqlStatementBuilder.InsertParameters()
                            .setEntityType(entityType)
                            .setCommandParameters(parameters)
                            .setRows(entitiesToRows(entityType, entities)));
            }
        });
        executor.execute(command);
    }

    @Override
    protected void delete(RepositorySession session, EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) throws IOException {
        deleteQuery()
                .where(entityType.getKeyField().in(entitiesToIds(entities)))
                .execute();
    }

    @Override
    protected void update(RepositorySession session, final EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) throws IOException {
        for (TEntity entity : entities) {
            updateQuery()
                    .where(entityType.getKeyField().equal(entity.getEntityId()))
                    .setAll(entity)
                    .execute();
        }
    }

    private Collection<TKey> entitiesToIds(Collection<TEntity> entities) {
        return transform(entities, new Function<TEntity, TKey>() {
            @Override
            public TKey apply(TEntity input) {
                return input.getEntityId();
            }
        });
    }

    private Collection<FieldValueLookup> entitiesToRows(final EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) {
        return transform(entities, new Function<TEntity, FieldValueLookup>() {
            @Override
            public FieldValueLookup apply(TEntity entity) {
                return new EntityFieldValueMap<>(entityType, entity);
            }
        });
    }
}
