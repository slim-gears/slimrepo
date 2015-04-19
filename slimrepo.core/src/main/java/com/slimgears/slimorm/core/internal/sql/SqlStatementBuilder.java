// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.interfaces.conditions.Condition;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimorm.core.internal.query.DeleteQueryParams;
import com.slimgears.slimorm.core.internal.query.InsertQueryParams;
import com.slimgears.slimorm.core.internal.query.SelectQueryParams;
import com.slimgears.slimorm.core.internal.query.UpdateQueryParams;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public interface SqlStatementBuilder {
    interface SyntaxProvider {
        <TEntity, T> String fieldName(Field<TEntity, T> field);
        <TEntity, T> String typeName(Field<TEntity, T> field);
        <TKey, TEntity extends Entity<TKey>> String tableName(EntityType<TKey, TEntity> entityType);
        String databaseName(RepositoryModel repositoryModel);
        String parameterReference(int index, String name);
        String valueToString(Object value);
        String substituteParameter(SqlCommand.Parameters params, Object value);
    }

    interface PredicateBuilder {
        <TEntity> String build(Condition<TEntity> condition, SqlCommand.Parameters parameters);
    }

    <TKey, TEntity extends Entity<TKey>> String countStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity extends Entity<TKey>> String selectStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity extends Entity<TKey>> String updateStatement(UpdateQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity extends Entity<TKey>> String deleteStatement(DeleteQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity extends Entity<TKey>> String insertStatement(InsertQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity extends Entity<TKey>> String createTableStatement(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>> String dropTableStatement(EntityType<TKey, TEntity> entityType);
}
