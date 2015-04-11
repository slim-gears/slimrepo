// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.interfaces.predicates.Predicate;
import com.slimgears.slimorm.internal.OrderFieldInfo;
import com.slimgears.slimorm.internal.UpdateFieldInfo;

import java.util.Collection;

import static com.google.common.collect.Iterables.transform;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public class SqlStatementBuilderImpl implements SqlStatementBuilder {
    private final PredicateBuilder predicateBuilder;
    private final SyntaxProvider syntaxProvider;

    public SqlStatementBuilderImpl(PredicateBuilder predicateBuilder, SyntaxProvider syntaxProvider) {
        this.predicateBuilder = predicateBuilder;
        this.syntaxProvider = syntaxProvider;
    }

    @Override
    public String buildCountStatement(CountParameters params) {
        return
                selectCountClause() +
                fromClause(params.entityType) +
                whereClause(params.predicate, params.commandParameters) +
                limitClause(params.limit, params.offset);
    }

    @Override
    public String buildSelectStatement(SelectParameters params) {
        return
                selectClause(params.entityType) +
                fromClause(params.entityType) +
                whereClause(params.predicate, params.commandParameters) +
                orderByClause(params.orderFields) +
                limitClause(params.limit, params.offset);
    }

    @Override
    public String buildUpdateStatement(UpdateParameters params) {
        return
                updateClause(params.entityType) +
                setClause(params.updateFields, params.commandParameters) +
                whereClause(params.predicate, params.commandParameters) +
                limitClause(params.limit, params.offset);
    }

    @Override
    public String buildDeleteStatement(DeleteParameters params) {
        return "DELETE " + fromClause(params.entityType) +
                whereClause(params.predicate, params.commandParameters) +
                limitClause(params.limit, params.offset);
    }

    @Override
    public String buildInsertStatement(InsertParameters params) {
        return
                insertClause(params.entityType) +
                valuesClause(params.entityType, params.commandParameters, params.rows);
    }

    @Override
    public String buildCreateTableStatement(CreateTableParameters params) {
        return null;
    }

    @Override
    public String buildDropTableStatement(DropTableParameters params) {
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

    private String limitClause(int limit, int offset) {
        if (limit == -1 && offset == 0) return "";
        String limitClause = "LIMIT " + limit;
        return offset == 0
                ? limitClause + "\n"
                : limitClause + " OFFSET " + offset + "\n";
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
        if (orderFields.isEmpty()) return "";
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
}
