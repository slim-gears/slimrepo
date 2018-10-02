package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Denis on 21-May-15.
 */
public class SimpleSqlDatabaseScheme implements SqlDatabaseScheme {
    private final String name;
    private final Map<String, TableScheme> tableSchemeMap = new LinkedHashMap<>();

    public SimpleSqlDatabaseScheme(String name, TableScheme[] tables) {
        this.name = name;
        for (TableScheme table : tables) {
            tableSchemeMap.put(table.getName(), table);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, TableScheme> getTables() {
        return tableSchemeMap;
    }

    @Override
    public TableScheme getTable(String name) {
        return tableSchemeMap.get(name);
    }

    /**
     * Created by Denis on 21-May-15.
     */
    public static class SimpleFieldScheme implements FieldScheme {
        private final TableScheme tableScheme;
        private final String name;
        private final String type;
        private final boolean nullable;
        private final boolean primaryKey;
        private final FieldScheme foreignField;
        private final Object defaultValue;

        public SimpleFieldScheme(
                TableScheme tableScheme,
                String name,
                String type,
                boolean nullable,
                boolean primaryKey,
                FieldScheme foreignField,
                Object defaultValue) {
            this.tableScheme = tableScheme;
            this.name = name;
            this.type = type;
            this.nullable = nullable;
            this.primaryKey = primaryKey;
            this.foreignField = foreignField;
            this.defaultValue = defaultValue;
        }

        @Override
        public TableScheme getTable() {
            return tableScheme;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public boolean isNotNull() {
            return !nullable;
        }

        @Override
        public boolean isPrimaryKey() {
            return primaryKey;
        }

        @Override
        public boolean isAutoIncremented() {
            return isPrimaryKey() && "INTEGER".equals(type);
        }

        @Override
        public boolean isForeignKey() {
            return foreignField != null;
        }

        @Override
        public FieldScheme getRelatedForeignField() {
            return foreignField;
        }
    }

    /**
     * Created by Denis on 21-May-15.
     */
    public static class SimpleTableScheme implements TableScheme {
        private final String catalog;
        private final String name;
        private final Map<String, FieldScheme> fields = new LinkedHashMap<>();
        private FieldScheme keyField;

        public SimpleTableScheme(String catalog, String name) {
            this.catalog = catalog;
            this.name = name;
        }

        @Override
        public String getCatalog() {
            return catalog;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<String, FieldScheme> getFields() {
            return fields;
        }

        @Override
        public FieldScheme getField(String name) {
            return fields.get(name);
        }

        @Override
        public FieldScheme getKeyField() {
            return keyField;
        }

        public FieldScheme addField(FieldScheme field) {
            fields.put(field.getName(), field);
            if (field.isPrimaryKey()) keyField = field;
            return field;
        }

        public FieldScheme addField(
                String name,
                String type,
                boolean nullable,
                boolean primaryKey,
                FieldScheme foreignField,
                Object defaultValue) {
            return addField(new SimpleFieldScheme(this, name, type, nullable, primaryKey, foreignField, defaultValue));
        }
    }
}
