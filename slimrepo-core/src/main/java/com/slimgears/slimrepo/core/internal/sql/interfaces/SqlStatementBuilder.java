// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
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
        String qualifiedFieldName(Field<?, ?> field);
        String simpleFieldName(Field<?, ?> field);
        String simpleFieldName(String name);
        String typeName(Field<?, ?> field);
        String tableName(EntityType<?, ?> entityType);
        String tableName(String name);
        String databaseName(RepositoryModel repositoryModel);
        String parameterReference(int index, String name);
        <T> String valueToString(Field<?, T> field, T value);
        <T> String substituteParameter(SqlCommand.Parameters params, Field<?, T> field, T value);
        String fieldAlias(Field<?, ?> field);
        String rawFieldAlias(Field<?, ?> field);
    }

    interface PredicateBuilder {
        <TEntity> String build(Condition<TEntity> condition, SqlCommand.Parameters parameters);
    }

    <TKey, TEntity> String countStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity> String selectStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity> String updateStatement(UpdateQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity> String deleteStatement(DeleteQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);
    <TKey, TEntity> String insertStatement(InsertQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams);

    String copyData(String fromTable, SqlDatabaseScheme.TableScheme toTable, Iterable<String> fieldNames);
    String cloneTableStatement(String existingTableName, String newTableName);
    String createTableStatement(SqlDatabaseScheme.TableScheme tableScheme);
    String dropTableStatement(String tableName);
}
