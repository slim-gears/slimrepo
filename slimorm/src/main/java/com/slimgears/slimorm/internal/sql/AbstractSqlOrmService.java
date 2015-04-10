// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.DeleteQuery;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.NumberField;
import com.slimgears.slimorm.interfaces.Field;
import com.slimgears.slimorm.interfaces.Query;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.StringField;
import com.slimgears.slimorm.interfaces.UpdateQuery;
import com.slimgears.slimorm.internal.EntityCache;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlOrmService implements SqlOrmService {
    private final SqlStatementBuilder statementBuilder = new SqlStatementBuilderImpl();

    @Override
    public <TKey, TEntity extends Entity<TKey>> Query<TEntity> createQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType) {
        return new SqlQuery<>(getSqlSession(session), cache, entityType, getStatementBuilder());
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> DeleteQuery<TEntity> createDeleteQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType) {
        return new SqlDeleteQuery<>(getSqlSession(session), cache, entityType, getStatementBuilder());
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> UpdateQuery<TEntity> createUpdateQuery(RepositorySession session, EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> entityType) {
        return new SqlUpdateQuery<>(getSqlSession(session), cache, entityType, getStatementBuilder());
    }

    @Override
    public <TEntity, T> Field<TEntity, T> createDataField(Class<TEntity> entityClass, String name, Class<T> type) {
        return new SqlDataField<>(name, type);
    }

    @Override
    public <TEntity, T> NumberField<TEntity, T> createNumberField(Class<TEntity> entityClass, String name, Class<T> numberType) {
        return new SqlNumberField<>(name, numberType);
    }

    @Override
    public <TEntity> StringField<TEntity> createStringField(Class<TEntity> entityClass, String name) {
        return new SqlStringField<>(name);
    }

    @Override
    public SqlStatementBuilder getStatementBuilder() {
        return statementBuilder;
    }

    protected SqlRepositorySession getSqlSession(RepositorySession session) {
        return (SqlRepositorySession)session;
    }
}
