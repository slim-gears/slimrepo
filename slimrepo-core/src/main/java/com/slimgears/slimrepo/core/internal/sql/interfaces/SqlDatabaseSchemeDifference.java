package com.slimgears.slimrepo.core.internal.sql.interfaces;

import java.util.Map;

/**
 * Created by Denis on 22-May-15.
 */
public interface SqlDatabaseSchemeDifference {
    interface TableSchemeDifference {
        SqlDatabaseScheme.TableScheme getOldTableScheme();
        SqlDatabaseScheme.TableScheme getNewTableScheme();
        Map<String, SqlDatabaseScheme.FieldScheme> getAddedFields();
        Map<String, SqlDatabaseScheme.FieldScheme> getDeletedFields();
        Map<String, SqlDatabaseScheme.FieldScheme> getModifiedFields();
    }

    SqlDatabaseScheme getOldDatabaseScheme();
    SqlDatabaseScheme getNewDatabaseScheme();

    Map<String, SqlDatabaseScheme.TableScheme> getAddedTables();
    Map<String, SqlDatabaseScheme.TableScheme> getDeletedTables();
    Map<String, TableSchemeDifference> getModifiedTables();

    boolean isEmpty();
}
