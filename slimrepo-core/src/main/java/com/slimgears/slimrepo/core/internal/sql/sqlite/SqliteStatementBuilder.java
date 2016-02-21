// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.conditions.RelationalCondition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.EntityFieldValueMap;
import com.slimgears.slimrepo.core.internal.OrderFieldInfo;
import com.slimgears.slimrepo.core.internal.PredicateVisitor;
import com.slimgears.slimrepo.core.internal.UpdateFieldInfo;
import com.slimgears.slimrepo.core.internal.query.DeleteQueryParams;
import com.slimgears.slimrepo.core.internal.query.InsertQueryParams;
import com.slimgears.slimrepo.core.internal.query.QueryPagination;
import com.slimgears.slimrepo.core.internal.query.SelectQueryParams;
import com.slimgears.slimrepo.core.internal.query.UpdateQueryParams;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
@SuppressWarnings("StaticPseudoFunctionalStyleMethod")
class SqliteStatementBuilder implements SqlStatementBuilder {
    private final PredicateBuilder predicateBuilder;
    private final SyntaxProvider syntaxProvider;

    public SqliteStatementBuilder(PredicateBuilder predicateBuilder, SyntaxProvider syntaxProvider) {
        this.predicateBuilder = predicateBuilder;
        this.syntaxProvider = syntaxProvider;
    }

    @Override
    public <TKey, TEntity> String countStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        Iterable<RelationalField> relationalFields = findRelationalFieldsInCondition(params.condition);
        return
                selectCountClause() +
                fromClause(params.entityType) +
                joinClauses(relationalFields) +
                whereClause(params.condition, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity> String selectStatement(SelectQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        Iterable<RelationalField> relationalFields = (params.fields != null)
                ? findRelationalFields(params.fields)
                : getAllRelationalFields(params.entityType);
        Iterable<Field<TEntity, ?>> fields = params.fields != null
                ? params.fields
                : params.entityType.getFields();

        return
                selectClause(params.entityType, fields) +
                fromClause(params.entityType) +
                joinClauses(relationalFields) +
                whereClause(params.condition, sqlParams) +
                orderByClause(params.order) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity> String updateStatement(UpdateQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return
                updateClause(params.entityType) +
                setClause(params.updates, sqlParams) +
                whereClause(params.condition, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity> String deleteStatement(DeleteQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        return "DELETE " + fromClause(params.entityType) +
                whereClause(params.condition, sqlParams) +
                limitClause(params.pagination);
    }

    @Override
    public <TKey, TEntity> String insertStatement(InsertQueryParams<TKey, TEntity> params, SqlCommand.Parameters sqlParams) {
        Stream<Field> fields = fieldsToInsert(params.entityType);
        return
                insertClause(params.entityType, fields) +
                valuesClause(fields, sqlParams, entitiesToRows(params.entityType, Stream.of(params.entities)));
    }

    @Override
    public String copyData(String fromTable, SqlDatabaseScheme.TableScheme toTable, Iterable<String> fieldNames) {
        Set<String> fieldNameSet = Stream.of(fieldNames).collect(Collectors.toSet());
        Collection<String> toFields = Stream.of(toTable.getFields().values())
                .map(SqlDatabaseScheme.FieldScheme::getName)
                .collect(Collectors.toList());

        Stream<String> fromFields = Stream.of(toFields)
                .map(field -> fieldNameSet.contains(field) ? syntaxProvider.simpleFieldName(field) : toTable.getField(field).getDefaultValue().toString());

        return
                insertClause(toTable.getName(), Stream.of(toFields)) +
                selectFromClause(fromTable, fromFields);
    }

    @Override
    public String cloneTableStatement(String existingTableName, String newTableName) {
        return "CREATE TABLE " + syntaxProvider.tableName(newTableName) + " AS SELECT * FROM " + syntaxProvider.tableName(existingTableName);
    }

    @Override
    public String createTableStatement(SqlDatabaseScheme.TableScheme tableScheme) {
        return
                "CREATE TABLE IF NOT EXISTS " + syntaxProvider.tableName(tableScheme.getName()) + " (\n    " +
                        columnDefinitions(tableScheme) + ")";
    }

    @Override
    public String dropTableStatement(String name) {
        return "DROP TABLE IF EXISTS " + syntaxProvider.tableName(name);
    }

    protected String insertClause(EntityType entityType, Stream<Field> fields) {
        return "INSERT INTO " +
                syntaxProvider.tableName(entityType) +
                " (" + fields.map(this::fieldName).collect(Collectors.joining(", ")) + ")\n";
    }

    protected String insertClause(String tableName, Stream<String> fieldNames) {
        return "INSERT INTO " +
                syntaxProvider.tableName(tableName) +
                " (" + fieldNames.map(syntaxProvider::simpleFieldName).collect(Collectors.joining(", ")) + ")\n";
    }

    protected String valuesClause(final Stream<Field> fields, final SqlCommand.Parameters parameters, Stream<FieldValueLookup> rows) {
        //noinspection unchecked
        return "VALUES " +
                rows
                        .map(row -> "(" + fields
                                .map(field -> substituteParameter(parameters, field, row.getValue(field)))
                                .collect(Collectors.joining(", ")) + ")")
                        .collect(Collectors.joining(", "));
    }

    private Stream<Field> fieldsToInsert(final EntityType entityType) {
        //noinspection unchecked
        return Stream.of(entityType.getFields()).filter(field -> !isAutoIncremented(entityType, (Field)field));
    }

    private String limitClause(QueryPagination pagination) {
        if (pagination == null || (pagination.limit == -1 && pagination.offset == 0)) return "";
        String limitClause = "LIMIT " + pagination.limit;
        return pagination.offset == 0
                ? limitClause + "\n"
                : limitClause + " OFFSET " + pagination.offset + "\n";
    }

    private String whereClause(Condition condition, SqlCommand.Parameters parameters) {
        if (condition == null) return "";
        String strPredicate = predicateBuilder.build(condition, parameters);
        return "WHERE " + strPredicate + "\n";
    }

    private String joinClauses(Iterable<RelationalField> relationalFields) {
        StringBuilder builder = new StringBuilder();
        addJoinClauses(builder, new HashSet<>(), relationalFields);
        return builder.toString();
    }

    private void addJoinClauses(StringBuilder builder, Set<EntityType> processedTypes, EntityType entityType) {
        if (processedTypes.add(entityType)) {
            //noinspection unchecked
            addJoinClauses(builder, processedTypes, entityType.getRelationalFields());
        }
    }

    private void addJoinClauses(StringBuilder builder, Set<EntityType> processedTypes, Iterable<RelationalField> relationalFields) {
        for (RelationalField relationalField : relationalFields) {
            builder.append(joinClause(relationalField));
            builder.append('\n');

            addJoinClauses(builder, processedTypes, relationalField.metaInfo().getRelatedEntityType());
        }
    }

    private String joinClause(RelationalField<?, ?> relationalField) {
        RelationalField.MetaInfo<?> relationalFieldMeta = relationalField.metaInfo();
        return "LEFT JOIN " +
                syntaxProvider.tableName(relationalFieldMeta.getRelatedEntityType()) +
                " ON " +  qualifiedFieldName(relationalField) + " = " + qualifiedFieldName(relationalFieldMeta.getRelatedEntityType().getKeyField());
    }

    private String selectCountClause() {
        return "SELECT COUNT(*)\n";
    }

    private <TEntity> String selectClause(EntityType entityType, Iterable<Field<TEntity, ?>> fields) {
        return "SELECT\n    " + allRelatedFields(entityType, Stream.of(fields))
                .map(this::fieldAsAlias)
                .collect(Collectors.joining(",\n    ")) + "\n";
    }

    private String selectFromClause(String fromTable, Stream<String> fieldNames) {
        return "SELECT " + fieldNames.collect(Collectors.joining(", ")) + " " +
                "FROM " + syntaxProvider.tableName(fromTable);
    }

    private String fieldAsAlias(Field field) {
        return syntaxProvider.qualifiedFieldName(field) + " AS " + syntaxProvider.fieldAlias(field);
    }

    private String orderByClause(Collection<OrderFieldInfo> orderFields) {
        if (orderFields == null || orderFields.isEmpty()) return "";
        return "ORDER BY " + Stream
                .of(orderFields)
                .map(orderField -> qualifiedFieldName(orderField.field) + " " + (orderField.ascending ? "ASC" : "DESC"))
                .collect(Collectors.joining(", ")) + "\n";
    }

    private String setClause(final Collection<UpdateFieldInfo> updateFields, final SqlCommand.Parameters parameters) {
        if (updateFields == null || updateFields.isEmpty()) return "";
        //noinspection unchecked
        return "SET " + Stream
                .of(updateFields)
                .map(updateField -> fieldName(updateField.field) + " = " + substituteParameter(parameters, updateField.field, updateField.value))
                .collect(Collectors.joining(", ")) + "\n";
    }

    private String updateClause(EntityType entityType) {
        return "UPDATE " + syntaxProvider.tableName(entityType) + "\n";
    }

    private String fromClause(EntityType entityType) {
        return "FROM " + syntaxProvider.tableName(entityType) + "\n";
    }

    private String columnDefinition(SqlDatabaseScheme.FieldScheme field) {
        String columnDef = syntaxProvider.simpleFieldName(field.getName()) + " " + field.getType();
        String constraints = columnConstraints(field);
        return  constraints.isEmpty()
                ? columnDef
                : columnDef + " " + constraints;
    }

    private String fieldName(Field field) {
        return syntaxProvider.simpleFieldName(field);
    }

    private <TKey, TEntity> Stream<FieldValueLookup> entitiesToRows(final EntityType<TKey, TEntity> entityType, Stream<TEntity> entities) {
        return entities.map(entity -> new EntityFieldValueMap<>(entityType, entity));
    }

    private String columnDefinitions(final SqlDatabaseScheme.TableScheme tableScheme) {
        return Stream.of(tableScheme.getFields().values()).map(this::columnDefinition).collect(Collectors.joining(",\n    "));
    }

    private String columnConstraints(SqlDatabaseScheme.FieldScheme field) {
        if (field.isAutoIncremented()) return "PRIMARY KEY ASC";
        if (field.isPrimaryKey()) return "PRIMARY KEY";
        if (field.isForeignKey()) return foreignKeyConstraint(field);
        if (field.isNotNull()) return "NOT NULL";
        return "";
    }

    private String foreignKeyConstraint(SqlDatabaseScheme.FieldScheme field) {
        SqlDatabaseScheme.TableScheme relatedTable = field.getRelatedForeignField().getTable();
        String relatedTableName = relatedTable.getName();
        String relatedKeyFieldName = relatedTable.getKeyField().getName();
        return "REFERENCES " + syntaxProvider.tableName(relatedTableName) + " (" + syntaxProvider.simpleFieldName(relatedKeyFieldName) + ")";
    }

    private boolean isAutoIncremented(EntityType entityType, Field field) {
        return entityType.getKeyField() == field && field instanceof ComparableField;
    }

    private <T> String substituteParameter(SqlCommand.Parameters parameters, Field<?, T> field, T value) {
        return syntaxProvider.substituteParameter(parameters, field, value);
    }

    private String qualifiedFieldName(Field<?, ?> field) {
        return syntaxProvider.qualifiedFieldName(field);
    }

    private <TEntity> Stream<Field> allRelatedFields(EntityType entityType, Stream<Field<TEntity, ?>> selectedFields) {
        Set<Field> fields = new LinkedHashSet<>();
        addAllRelatedFields(fields, new HashSet<>(), entityType, selectedFields);
        return Stream.of(fields);
    }

    private void addAllRelatedFields(Set<Field> fields, Set<EntityType> processedEntityTypes, EntityType entityType) {
        //noinspection unchecked
        addAllRelatedFields(fields, processedEntityTypes, entityType, Stream.of(entityType.getFields()));
    }

    private <TEntity> void addAllRelatedFields(Set<Field> fields, Set<EntityType> processedEntityTypes, EntityType entityType, Stream<Field<TEntity, ?>> selectedFields) {
        if (!processedEntityTypes.add(entityType)) return;

        selectedFields
                .filter(fields::add)
                .filter(field -> field instanceof RelationalField)
                .map(field -> (RelationalField)field)
                .forEach(field -> addAllRelatedFields(fields, processedEntityTypes, field.metaInfo().getRelatedEntityType()));
    }

    private Iterable<RelationalField> findRelationalFieldsInCondition(Condition<?> condition) {
        final Set<RelationalField> relationalFields = new LinkedHashSet<>();
        PredicateVisitor visitor = new PredicateVisitor() {
            @Override
            public Object visitRelational(RelationalCondition relationalCondition) {
                if (relationalFields.add(relationalCondition.getField())) {
                    //noinspection unchecked
                    visit(relationalCondition.getCondition());
                }
                return null;
            }
        };
        //noinspection unchecked
        visitor.visit(condition);
        return relationalFields;
    }

    private <TEntity> Iterable<RelationalField> findRelationalFields(Iterable<Field<TEntity, ?>> fields) {
        List<RelationalField> relationalFields = new ArrayList<>();
        for (Field<?, ?> field : fields) {
            if (field instanceof RelationalField) {
                relationalFields.add((RelationalField<?, ?>)field);
            }
        }
        return relationalFields;
    }

    private Iterable<RelationalField> getAllRelationalFields(EntityType entityType) {
        //noinspection unchecked
        return entityType.getRelationalFields();
    }
}
