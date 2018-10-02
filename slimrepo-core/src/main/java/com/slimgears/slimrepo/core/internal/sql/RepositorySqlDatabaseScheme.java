package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Denis on 21-May-15.
 *
 */
class RepositorySqlDatabaseScheme implements SqlDatabaseScheme {
    private final static Map<Class, Object> DEFAULT_VALUES = new HashMap<>();
    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;
    private final RepositoryModel repositoryModel;
    private final Map<EntityType, TableScheme> tableSchemeMap = new LinkedHashMap<>();
    private final Map<String, TableScheme> nameToTableSchemeMap = new LinkedHashMap<>();

    static {
        DEFAULT_VALUES.put(int.class, 0);
        DEFAULT_VALUES.put(Integer.class, 0);
        DEFAULT_VALUES.put(float.class, 0.0);
        DEFAULT_VALUES.put(Float.class, 0.0);
        DEFAULT_VALUES.put(double.class, 0.0);
        DEFAULT_VALUES.put(Double.class, 0.0);
        DEFAULT_VALUES.put(long.class, 0L);
        DEFAULT_VALUES.put(Long.class, 0L);
        DEFAULT_VALUES.put(short.class, 0);
        DEFAULT_VALUES.put(Short.class, 0);
        DEFAULT_VALUES.put(String.class, "''");
    }

    class EntityTypeTableScheme<TKey, TEntity> implements TableScheme {
        private final EntityType<TKey, TEntity> entityType;
        private final Map<Field<TEntity, ?>, FieldScheme> fieldSchemeMap = new LinkedHashMap<>();
        private final Map<String, FieldScheme> nameToFieldSchemeMap = new LinkedHashMap<>();

        class EntityFieldScheme<T> implements FieldScheme {
            private final Field<TEntity, T> field;
            private final Field.MetaInfo<T> metaInfo;

            EntityFieldScheme(Field<TEntity, T> field) {
                this.field = field;
                this.metaInfo = field.metaInfo();
            }

            @Override
            public TableScheme getTable() {
                return EntityTypeTableScheme.this;
            }

            @Override
            public String getName() {
                return metaInfo.getName();
            }

            @Override
            public String getType() {
                return syntaxProvider.typeName(field);
            }

            @Override
            public Object getDefaultValue() {
                return isNotNull() ? DEFAULT_VALUES.get(metaInfo.getValueType()) : "NULL";
            }

            @Override
            public boolean isNotNull() {
                return !metaInfo.isNullable();
            }

            @Override
            public boolean isPrimaryKey() {
                return metaInfo.isKey();
            }

            @Override
            public boolean isAutoIncremented() {
                return metaInfo.isAutoIncremented();
            }

            @Override
            public boolean isForeignKey() {
                return field instanceof RelationalField;
            }

            @Override
            public FieldScheme getRelatedForeignField() {
                if (!isForeignKey()) return null;
                RelationalField relationalField = (RelationalField)field;

                EntityType<?, ?> relatedEntityType = relationalField.metaInfo().getRelatedEntityType();
                TableScheme relatedTableScheme = getTableScheme(relatedEntityType);
                return relatedTableScheme.getKeyField();
            }
        }

        EntityTypeTableScheme(EntityType<TKey, TEntity> entityType) {
            this.entityType = entityType;
            for (Field<TEntity, ?> field : entityType.getFields()) {
                FieldScheme fieldScheme = new EntityFieldScheme<>(field);
                fieldSchemeMap.put(field, fieldScheme);
                nameToFieldSchemeMap.put(fieldScheme.getName(), fieldScheme);
            }
        }

        @Override
        public String getCatalog() {
            return null;
        }

        @Override
        public String getName() {
            return entityType.getName();
        }

        @Override
        public Map<String, FieldScheme> getFields() {
            return nameToFieldSchemeMap;
        }

        @Override
        public FieldScheme getField(String name) {
            return nameToFieldSchemeMap.get(name);
        }

        @Override
        public FieldScheme getKeyField() {
            return getFieldScheme(entityType.getKeyField());
        }

        public FieldScheme getFieldScheme(Field<TEntity, ?> field) {
            return fieldSchemeMap.get(field);
        }
    }

    public RepositorySqlDatabaseScheme(SqlStatementBuilder.SyntaxProvider syntaxProvider, RepositoryModel repositoryModel) {
        this.syntaxProvider = syntaxProvider;
        this.repositoryModel = repositoryModel;
        for (EntityType<?, ?> entityType : repositoryModel.getEntityTypes()) {
            addTable(entityType);
        }
    }

    @Override
    public String getName() {
        return repositoryModel.getName();
    }

    @Override
    public Map<String, TableScheme> getTables() {
        return nameToTableSchemeMap;
    }

    @Override
    public TableScheme getTable(String name) {
        return nameToTableSchemeMap.get(name);
    }

    private <TKey, TEntity> TableScheme getTableScheme(EntityType<TKey, TEntity> entityType) {
        return tableSchemeMap.get(entityType);
    }

    private void addTable(EntityType<?, ?> entityType) {
        if (tableSchemeMap.containsKey(entityType)) return;

        for (RelationalField<?, ?> relatedEntityType : entityType.getRelationalFields()) {
            addTable(relatedEntityType.metaInfo().getRelatedEntityType());
        }

        TableScheme tableScheme = new EntityTypeTableScheme<>(entityType);
        tableSchemeMap.put(entityType, tableScheme);
        nameToTableSchemeMap.put(tableScheme.getName(), tableScheme);
    }
}
