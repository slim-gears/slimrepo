// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.entities.FieldValueLookup;
import com.slimgears.slimorm.interfaces.predicates.Predicate;
import com.slimgears.slimorm.internal.EntityFieldValueMap;
import com.slimgears.slimorm.internal.OrderFieldInfo;
import com.slimgears.slimorm.internal.UpdateFieldInfo;
import com.slimgears.slimorm.internal.query.DeleteQueryParams;
import com.slimgears.slimorm.internal.query.InsertQueryParams;
import com.slimgears.slimorm.internal.query.QueryPagination;
import com.slimgears.slimorm.internal.query.SelectQueryParams;
import com.slimgears.slimorm.internal.query.UpdateQueryParams;

import java.util.Collection;

import static com.google.common.collect.Iterables.transform;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public class DefaultSqlStatementBuilder implements SqlStatementBuilder {
    private final PredicateBuilder predicateBuilder;
    private final SyntaxProvider syntaxProvider;

    public DefaultSqlStatementBuilder(PredicateBuilder predicateBuilder, SyntaxProvider syntaxProvider) {
        this.predicateBuilder = predicateBuilder;
        this.syntaxProvider = syntaxProvider;
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String countStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return
                selectCountClause() +
                fromClause(params.entityType) +
                whereClause(params.predicate, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String selectStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return
                selectClause(params.entityType) +
                fromClause(params.entityType) +
                whereClause(params.predicate, sqlParams) +
                orderByClause(params.order) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String updateStatement(UpdateQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return
                updateClause(params.entityType) +
                setClause(params.updates, sqlParams) +
                whereClause(params.predicate, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String deleteStatement(DeleteQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return "DELETE " + fromClause(params.entityType) +
                whereClause(params.predicate, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String insertStatement(InsertQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return
                insertClause(params.entityType) +
                valuesClause(params.entityType, sqlParams, entitiesToRows(params.entityType, params.entities));
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String createTableStatement(EntityType<TKey, TEntity> entityType) {
        return null;
    }

    @Override
    public <TKey, TEntity extends Entity<TKey>> String dropTableStatement(EntityType<TKey, TEntity> params) {
        return null;
    }

    protected String insertClause(EntityType entityType) {
        return "INSERT INTO " +
                tableName(entityType) +
                " (" + Joiner.on(", ").join(fieldNames(entityType)) + ")\n";
    }

    protected String valuesClause(final EntityType entityType, final SqlCommand.Parameters parameters, Iterable<FieldValueLookup> rows) {
        return "VALUES \n" +
                Joiner.on(",\n").join(transform(rows, new Function<FieldValueLookup, String>() {
                    @Override
                    public String apply(final FieldValueLookup row) {
                        return "(" +
                                Joiner.on(", ").join(transform(entityType.getFields(),
                                        new Function<Field, String>() {
                                            @Override
                                            public String apply(Field field) {
                                                //noinspection unchecked
                                                return parameters.add(row.getValue(field));
                                            }
                        }));
                    }
                }));
    }

    private String limitClause(QueryPagination pagination) {
        if (pagination == null || (pagination.limit == -1 && pagination.offset == 0)) return "";
        String limitClause = "LIMIT " + pagination.limit;
        return pagination.offset == 0
                ? limitClause + "\n"
                : limitClause + " OFFSET " + pagination.offset + "\n";
    }

    private String whereClause(Predicate predicate, SqlCommand.Parameters parameters) {
        if (predicate == null) return "";
        String strPredicate = predicateBuilder.build(predicate, parameters);
        return "WHERE " + strPredicate + "\n";
    }

    private String selectCountClause() {
        return "SELECT COUNT(*)\n";
    }

    private String selectClause(EntityType entityType) {
        return "SELECT " + Joiner.on(", ").join(fieldNames(entityType)) + "\n";
    }

    private String orderByClause(Collection<OrderFieldInfo> orderFields) {
        if (orderFields == null || orderFields.isEmpty()) return "";
        return "ORDER BY " + Joiner.on(", ")
                .join(transform(orderFields,
                        new Function<OrderFieldInfo, String>() {
                            @Override
                            public String apply(OrderFieldInfo orderField) {
                                return fieldName(orderField.field) + " " + (orderField.ascending ? "ASC" : "DESC");
                            }
                        })) + "\n";
    }

    private String setClause(final Collection<UpdateFieldInfo> updateFields, final SqlCommand.Parameters parameters) {
        if (updateFields == null || updateFields.isEmpty()) return "";
        return "SET " + Joiner
                .on(", ")
                .join(transform(updateFields,
                        new Function<UpdateFieldInfo, String>() {
                            @Override
                            public String apply(UpdateFieldInfo updateField) {
                                return fieldName(updateField.field) + " = " + valueToString(parameters.add(updateField.value));
                            }
                        })) + "\n";
    }

    private String updateClause(EntityType entityType) {
        return "UPDATE " + tableName(entityType) + "\n";
    }

    private String fromClause(EntityType entityType) {
        return "FROM " + tableName(entityType) + "\n";
    }

    private String valueToString(Object value) {
        return syntaxProvider.valueToString(value);
    }

    private String tableName(EntityType entityType) {
        return syntaxProvider.tableName(entityType);
    }

    private Iterable<String> fieldNames(EntityType entityType) {
        //noinspection unchecked
        return fieldNames(entityType.getFields());
    }

    private Iterable<String> fieldNames(Iterable<Field> fields) {
        return transform(fields, new Function<Field, String>() {
            @Override
            public String apply(Field field) {
                return fieldName(field);
            }
        });
    }

    private String fieldName(Field field) {
        return syntaxProvider.fieldName(field);
    }

    private <TKey, TEntity extends Entity<TKey>> Collection<FieldValueLookup> entitiesToRows(final EntityType<TKey, TEntity> entityType, Collection<TEntity> entities) {
        return Collections2.transform(entities, new Function<TEntity, FieldValueLookup>() {
            @Override
            public FieldValueLookup apply(TEntity entity) {
                return new EntityFieldValueMap<>(entityType, entity);
            }
        });
    }

}
