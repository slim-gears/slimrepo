// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.query.DeleteQueryParams;
import com.slimgears.slimrepo.core.internal.query.InsertQueryParams;
import com.slimgears.slimrepo.core.internal.query.SelectQueryParams;
import com.slimgears.slimrepo.core.internal.query.UpdateQueryParams;

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
        String valueToString(Class valueType, Object value);
        String substituteParameter(SqlCommand.Parameters params, Class valueType, Object value);
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
