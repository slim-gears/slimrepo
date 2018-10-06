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
import com.slimgears.slimrepo.core.internal.query.*;
import com.slimgears.slimrepo.core.internal.sql.SqlCommands;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.util.*;

/**
 * Created by Denis on 08-Apr-15
 *
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
    public <TKey, TEntity> SqlCommand countStatement(SelectQueryParams<TKey, TEntity> params) {
        Iterable<RelationalField> relationalFields = findRelationalFieldsInCondition(params.condition);
        return SqlCommands.builder()
                .append(selectCountClause())
                .append(fromClause(params.entityType))
                .append(joinClauses(relationalFields))
                .append(b -> whereClause(params.condition, b))
                .append(limitClause(params.pagination))
                .build();
    }

    @Override
    public <TKey, TEntity> SqlCommand selectStatement(SelectQueryParams<TKey, TEntity> params) {
        Iterable<RelationalField> relationalFields = (params.fields != null)
                ? findRelationalFields(params.fields)
                : getAllRelationalFields(params.entityType);
        Iterable<Field<TEntity, ?>> fields = params.fields != null
                ? params.fields
                : params.entityType.getFields();

        return SqlCommands.builder()
                .append(selectClause(params.entityType, fields))
                .append(fromClause(params.entityType))
                .append(joinClauses(relationalFields))
                .append(builder -> whereClause(params.condition, builder))
                .append(orderByClause(params.order))
                .append(limitClause(params.pagination))
                .build();
    }

    @Override
    public <TKey, TEntity> SqlCommand updateStatement(UpdateQueryParams<TKey, TEntity> params) {
        return SqlCommands.builder()
                .append(updateClause(params.entityType))
                .append(b -> setClause(params.updates, b))
                .append(b -> whereClause(params.condition, b))
                .append(limitClause(params.pagination))
                .build();
    }

    @Override
    public <TKey, TEntity> SqlCommand deleteStatement(DeleteQueryParams<TKey, TEntity> params) {
        return SqlCommands.builder()
                .append("DELETE " + fromClause(params.entityType))
                .append(b -> whereClause(params.condition, b))
                .append(limitClause(params.pagination))
                .build();
    }

    @Override
    public <TKey, TEntity> SqlCommand insertStatement(InsertQueryParams<TKey, TEntity> params) {
        Collection<Field<TEntity, ?>> fields = fieldsToInsert(params.entityType);
        return SqlCommands.builder()
                .append(insertClause(params.entityType, fields))
                .append(b -> valuesClause(fields, b, entitiesToRows(params.entityType, Stream.of(params.entities))))
                .build();
    }

    @Override
    public SqlCommand copyData(String fromTable, SqlDatabaseScheme.TableScheme toTable, Iterable<String> fieldNames) {
        Set<String> fieldNameSet = Stream.of(fieldNames).collect(Collectors.toSet());
        Collection<String> toFields = Stream.of(toTable.getFields().values())
                .map(SqlDatabaseScheme.FieldScheme::getName)
                .collect(Collectors.toList());

        Stream<String> fromFields = Stream.of(toFields)
                .map(field -> fieldNameSet.contains(field) ? syntaxProvider.simpleFieldName(field) : toTable.getField(field).getDefaultValue().toString());

        return SqlCommands.builder()
                .append(insertClause(toTable.getName(), toFields))
                .append(selectFromClause(fromTable, fromFields))
                .build();
    }

    @Override
    public SqlCommand cloneTableStatement(String existingTableName, String newTableName) {
        return SqlCommands.builder()
                .append("CREATE TABLE ")
                .append(syntaxProvider.tableName(newTableName))
                .append(" AS SELECT * FROM ")
                .append(syntaxProvider.tableName(existingTableName))
                .build();
    }

    @Override
    public SqlCommand createTableStatement(SqlDatabaseScheme.TableScheme tableScheme) {
        return SqlCommands.builder()
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(syntaxProvider.tableName(tableScheme.getName()))
                .append(" (\n    ")
                .append(columnDefinitions(tableScheme))
                .append(")")
                .build();
    }

    @Override
    public SqlCommand dropTableStatement(String name) {
        return SqlCommands.builder()
                .append("DROP TABLE IF EXISTS ")
                .append(syntaxProvider.tableName(name))
                .build();
    }

    protected <T> String insertClause(EntityType<?, T> entityType, Iterable<Field<T, ?>> fields) {
        return SqlCommands.builder()
                .append("INSERT INTO ")
                .append(syntaxProvider.tableName(entityType))
                .append(" (")
                .append(Stream.of(fields).map(this::fieldName).collect(Collectors.joining(", ")))
                .append(")\n")
                .build().getStatement();
    }

    protected String insertClause(String tableName, Iterable<String> fieldNames) {
        return SqlCommands.builder()
                .append("INSERT INTO ")
                .append(syntaxProvider.tableName(tableName))
                .append(" (")
                .append(Stream.of(fieldNames).map(syntaxProvider::simpleFieldName).collect(Collectors.joining(", ")))
                .append(")\n")
                .build().getStatement();
    }

    protected <TEntity> String valuesClause(final Iterable<Field<TEntity, ?>> fields, final SqlCommand.Builder sqlBuilder, Iterable<FieldValueLookup> rows) {
        //noinspection unchecked
        return "VALUES " +
                Stream.of(rows)
                        .map(row -> "(" + Stream.of(fields)
                                .map(field -> substituteParameter(sqlBuilder, (Field<TEntity, Object>)field, row.getValue(field)))
                                .collect(Collectors.joining(", ")) + ")")
                        .collect(Collectors.joining(", "));
    }

    private <TEntity> Collection<Field<TEntity, ?>> fieldsToInsert(final EntityType<?, TEntity> entityType) {
        return Stream
                .of(entityType.getFields())
                .filter(field -> !isAutoIncremented(entityType, field))
                .collect(Collectors.toList());
    }

    private String limitClause(QueryPagination pagination) {
        if (pagination == null || (pagination.limit == -1 && pagination.offset == 0)) return "";
        String limitClause = "LIMIT " + pagination.limit;
        return pagination.offset == 0
                ? limitClause + "\n"
                : limitClause + " OFFSET " + pagination.offset + "\n";
    }

    private <TEntity> String whereClause(Condition<TEntity> condition, SqlCommand.Builder sqlBuilder) {
        if (condition == null) return "";
        String strPredicate = predicateBuilder.build(condition, sqlBuilder);
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

    private String setClause(final Collection<UpdateFieldInfo> updateFields, final SqlCommand.Builder sqlBuilder) {
        if (updateFields == null || updateFields.isEmpty()) return "";
        //noinspection unchecked
        return "SET " + Stream
                .of(updateFields)
                .map(updateField -> fieldName(updateField.field) + " = " + substituteParameter(sqlBuilder, updateField.field, updateField.value))
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

    private <TKey, TEntity> Collection<FieldValueLookup> entitiesToRows(final EntityType<TKey, TEntity> entityType, Stream<TEntity> entities) {
        return entities.map(entity -> new EntityFieldValueMap<>(entityType, entity)).collect(Collectors.toList());
    }

    private String columnDefinitions(final SqlDatabaseScheme.TableScheme tableScheme) {
        return Stream.of(tableScheme.getFields().values()).map(this::columnDefinition).collect(Collectors.joining(",\n    "));
    }

    private String columnConstraints(SqlDatabaseScheme.FieldScheme field) {
        if (field.isAutoIncremented()) return "PRIMARY KEY ASC NOT NULL";
        if (field.isPrimaryKey()) return "PRIMARY KEY NOT NULL";
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

    private <TEntity> boolean isAutoIncremented(EntityType<?, TEntity> entityType, Field<TEntity, ?> field) {
        return entityType.getKeyField() == field && field instanceof ComparableField;
    }

    private <TEntity, T> String substituteParameter(SqlCommand.Builder sqlBuilder, Field<TEntity, T> field, T value) {
        return syntaxProvider.substituteParameter(sqlBuilder, field, value);
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
