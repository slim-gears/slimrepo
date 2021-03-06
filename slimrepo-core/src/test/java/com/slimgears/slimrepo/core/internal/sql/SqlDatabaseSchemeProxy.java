package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

/**
 * Created by Denis on 25-May-15.
 *
 */
class SqlDatabaseSchemeProxy implements SqlDatabaseScheme {
    private final SqlDatabaseScheme databaseScheme;
    private Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap;

    private final Set<String> hiddenTables = new HashSet<>();
    private final Set<String> hiddenFields = new HashSet<>();

    SqlDatabaseSchemeProxy(SqlDatabaseScheme databaseScheme) {
        this.databaseScheme = databaseScheme;
    }

    public void hideTables(EntityType<?, ?>... entityTypes) {
        hiddenTables.addAll(Stream.of(entityTypes).map(EntityType::getName).collect(Collectors.toList()));
    }

    @SafeVarargs
    public final <TKey, TEntity> void hideFields(final EntityType<TKey, TEntity> entityType, Field<TEntity, ?>... fields) {
        hiddenFields.addAll(Stream.of(fields)
                                    .map(field -> fullFieldName(entityType.getName(), field.metaInfo().getName()))
                                    .collect(Collectors.toList()));
    }

    private String fullFieldName(String tableName, String fieldName) {
        return tableName + "." + fieldName;
    }

    @Override
    public String getName() {
        return databaseScheme.getName();
    }

    @Override
    public Map<String, SqlDatabaseScheme.TableScheme> getTables() {
        if (tableSchemeMap == null) {
            tableSchemeMap = new LinkedHashMap<>();
            Stream.of(databaseScheme.getTables().values())
                    .filter(table -> !hiddenTables.contains(table.getName()))
                    .forEach(table -> tableSchemeMap.put(table.getName(), new TableScheme(table)));
        }
        return tableSchemeMap;
    }

    @Override
    public SqlDatabaseScheme.TableScheme getTable(String name) {
        return getTables().get(name);
    }

    class TableScheme implements SqlDatabaseScheme.TableScheme {
        private final SqlDatabaseScheme.TableScheme tableScheme;
        private final Map<String, SqlDatabaseScheme.FieldScheme> fieldSchemeMap = new LinkedHashMap<>();

        TableScheme(SqlDatabaseScheme.TableScheme tableScheme) {
            this.tableScheme = tableScheme;
            for (SqlDatabaseScheme.FieldScheme field : tableScheme.getFields().values()) {
                String fullFieldName = fullFieldName(field.getTable().getName(), field.getName());
                if (!hiddenFields.contains(fullFieldName)) {
                    fieldSchemeMap.put(field.getName(), new TableScheme.FieldScheme(field));
                }
            }
        }

        @Override
        public String getName() {
            return tableScheme.getName();
        }

        @Override
        public Map<String, SqlDatabaseScheme.FieldScheme> getFields() {
            return fieldSchemeMap;
        }

        @Override
        public SqlDatabaseScheme.FieldScheme getField(String name) {
            return fieldSchemeMap.get(name);
        }

        @Override
        public SqlDatabaseScheme.FieldScheme getKeyField() {
            return getField(tableScheme.getKeyField().getName());
        }

        class FieldScheme implements SqlDatabaseScheme.FieldScheme {
            private final SqlDatabaseScheme.FieldScheme fieldScheme;

            FieldScheme(SqlDatabaseScheme.FieldScheme fieldScheme) {
                this.fieldScheme = fieldScheme;
            }

            @Override
            public SqlDatabaseScheme.TableScheme getTable() {
                return TableScheme.this;
            }

            @Override
            public String getName() {
                return fieldScheme.getName();
            }

            @Override
            public String getType() {
                return fieldScheme.getType();
            }

            @Override
            public Object getDefaultValue() {
                return fieldScheme.getDefaultValue();
            }

            @Override
            public boolean isNotNull() {
                return fieldScheme.isNotNull();
            }

            @Override
            public boolean isPrimaryKey() {
                return fieldScheme.isPrimaryKey();
            }

            @Override
            public boolean isAutoIncremented() {
                return fieldScheme.isAutoIncremented();
            }

            @Override
            public boolean isForeignKey() {
                return fieldScheme.isForeignKey();
            }

            @Override
            public SqlDatabaseScheme.FieldScheme getRelatedForeignField() {
                SqlDatabaseScheme.FieldScheme relatedField = fieldScheme.getRelatedForeignField();
                SqlDatabaseScheme.TableScheme relatedTable = SqlDatabaseSchemeProxy.this.getTable(relatedField.getTable().getName());
                return relatedTable.getField(relatedField.getName());
            }
        }
    }
}
