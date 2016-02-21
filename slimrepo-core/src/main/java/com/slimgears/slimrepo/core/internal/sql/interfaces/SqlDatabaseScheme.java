package com.slimgears.slimrepo.core.internal.sql.interfaces;

import java.util.Map;

/**
 * Created by Denis on 22-May-15.
 *
 */
public interface SqlDatabaseScheme {
    interface FieldScheme {
        TableScheme getTable();
        String getName();
        String getType();
        Object getDefaultValue();
        boolean isNotNull();
        boolean isPrimaryKey();
        boolean isAutoIncremented();
        boolean isForeignKey();
        FieldScheme getRelatedForeignField();
    }

    interface TableScheme {
        String getName();
        Map<String, FieldScheme> getFields();
        FieldScheme getField(String name);
        FieldScheme getKeyField();
    }

    String getName();
    Map<String, TableScheme> getTables();
    TableScheme getTable(String name);
}
