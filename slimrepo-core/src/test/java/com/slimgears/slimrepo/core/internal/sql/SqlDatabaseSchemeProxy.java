package com.slimgears.slimrepo.core.internal.sql;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Denis on 25-May-15.
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
        hiddenTables.addAll(
                Collections2.transform(
                        Arrays.asList(entityTypes),
                        new Function<EntityType<?, ?>, String>() {
                            @Override
                            public String apply(EntityType<?, ?> entityType) {
                                return entityType.getName();
                            }
                        }));
    }

    public <TKey, TEntity extends Entity<TKey>> void hideFields(final EntityType<TKey, TEntity> entityType, Field<TEntity, ?>... fields) {
        hiddenFields.addAll(
                Collections2.transform(
                        Arrays.asList(fields),
                        new Function<Field<TEntity, ?>, String>() {
                            @Override
                            public String apply(Field<TEntity, ?> field) {
                                return fullFieldName(entityType.getName(), field.metaInfo().getName());
                            }
                        }));
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
            for (SqlDatabaseScheme.TableScheme tableScheme : databaseScheme.getTables().values()) {
                if (!hiddenTables.contains(tableScheme.getName())) {
                    tableSchemeMap.put(tableScheme.getName(), new TableScheme(tableScheme));
                }
            }
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
