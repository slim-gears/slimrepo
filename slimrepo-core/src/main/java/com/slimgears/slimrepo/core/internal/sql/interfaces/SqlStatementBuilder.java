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
 *
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
        String parameterReference(int index);
        <T> String valueToString(Field<?, T> field, T value);
        <T> String substituteParameter(SqlCommand.Builder params, Field<?, T> field, T value);
        String fieldAlias(Field<?, ?> field);
        String rawFieldAlias(Field<?, ?> field);
    }

    interface PredicateBuilder {
        <TEntity> String build(Condition<TEntity> condition, SqlCommand.Builder sqlCommandBuilder);
    }

    <TKey, TEntity> SqlCommand countStatement(SelectQueryParams<TKey, TEntity> params);
    <TKey, TEntity> SqlCommand selectStatement(SelectQueryParams<TKey, TEntity> params);
    <TKey, TEntity> SqlCommand updateStatement(UpdateQueryParams<TKey, TEntity> params);
    <TKey, TEntity> SqlCommand deleteStatement(DeleteQueryParams<TKey, TEntity> params);
    <TKey, TEntity> SqlCommand insertStatement(InsertQueryParams<TKey, TEntity> params);

    SqlCommand copyData(String fromTable, SqlDatabaseScheme.TableScheme toTable, Iterable<String> fieldNames);
    SqlCommand cloneTableStatement(String existingTableName, String newTableName);
    SqlCommand createTableStatement(SqlDatabaseScheme.TableScheme tableScheme);
    SqlCommand dropTableStatement(String tableName);
}
