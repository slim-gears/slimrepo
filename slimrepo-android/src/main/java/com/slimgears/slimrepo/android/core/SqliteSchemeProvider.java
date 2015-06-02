package com.slimgears.slimrepo.android.core;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.SimpleSqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Denis on 21-May-15.
 */
public class SqliteSchemeProvider extends AbstractSqlSchemeProvider {
    private final static Set<String> IGNORED_TABLES = new HashSet<>(Arrays.asList("android_metadata"));

    private final static String SQL_GET_TABLE_NAMES = "SELECT `name` FROM `sqlite_master`";
    private final static String SQL_GET_FOREIGN_KEY_LIST = "PRAGMA foreign_key_list(`%s`)";
    private final static String SQL_GET_TABLE_SCHEME = "PRAGMA table_info(`%s`)";

    private final static int TABLE_SCHEME_FIELD_NAME = 1;
    private final static int TABLE_SCHEME_FIELD_TYPE = 2;
    private final static int TABLE_SCHEME_FIELD_NOT_NULL = 3;
    private final static int TABLE_SCHEME_FIELD_PRIMARY_KEY = 5;

    private final static int FOREIGN_KEY_TABLE_NAME = 2;
    private final static int FOREIGN_KEY_FROM_FIELD = 3;
    private final static int FOREIGN_KEY_TO_FIELD = 4;

    private final SQLiteDatabase database;

    public SqliteSchemeProvider(SqlStatementBuilder.SyntaxProvider syntaxProvider, SQLiteDatabase database) {
        super(syntaxProvider);
        this.database = database;
    }

    private SqlDatabaseScheme.TableScheme[] getTables() {
        Cursor cursor = database.rawQuery(SQL_GET_TABLE_NAMES, null);
        if (cursor == null) return new SqlDatabaseScheme.TableScheme[0];

        try {
            cursor.moveToFirst();

            Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap = new LinkedHashMap<>();

            while (!cursor.isAfterLast()) {
                String tableName = cursor.getString(0);
                if (!IGNORED_TABLES.contains(tableName)) {
                    getTableScheme(tableSchemeMap, tableName);
                }
                cursor.moveToNext();
            }

            return tableSchemeMap.values().toArray(new SqlDatabaseScheme.TableScheme[tableSchemeMap.size()]);
        } finally {
            cursor.close();
        }
    }

    private SqlDatabaseScheme.TableScheme getTableScheme(Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap, String tableName) {
        if (tableSchemeMap.containsKey(tableName)) {
            return tableSchemeMap.get(tableName);
        }

        SimpleSqlDatabaseScheme.SimpleTableScheme tableScheme = new SimpleSqlDatabaseScheme.SimpleTableScheme(tableName);
        Cursor cursor = database.rawQuery(String.format(SQL_GET_TABLE_SCHEME, tableName), null);

        try {
            cursor.moveToFirst();
            Map<String, SqlDatabaseScheme.FieldScheme> foreignKeys = getForeignFields(tableSchemeMap, tableName);

            while (!cursor.isAfterLast()) {
                String fieldName = cursor.getString(TABLE_SCHEME_FIELD_NAME);
                String fieldType = cursor.getString(TABLE_SCHEME_FIELD_TYPE);
                boolean notNull = cursor.getInt(TABLE_SCHEME_FIELD_NOT_NULL) != 0;
                boolean primaryKey = cursor.getInt(TABLE_SCHEME_FIELD_PRIMARY_KEY) != 0;
                SqlDatabaseScheme.FieldScheme foreignField = foreignKeys.get(fieldName);
                tableScheme.addField(fieldName, fieldType, notNull, primaryKey, foreignField);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        tableSchemeMap.put(tableName, tableScheme);

        return tableScheme;
    }

    private Map<String, SqlDatabaseScheme.FieldScheme> getForeignFields(Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap, String tableName) {
        Cursor cursor = database.rawQuery(String.format(SQL_GET_FOREIGN_KEY_LIST, tableName), null);

        try {
            cursor.moveToFirst();
            Map<String, SqlDatabaseScheme.FieldScheme> foreignFields = new LinkedHashMap<>();

            while (!cursor.isAfterLast()) {
                String foreignTableName = cursor.getString(FOREIGN_KEY_TABLE_NAME);
                String fromFieldName = cursor.getString(FOREIGN_KEY_FROM_FIELD);
                String toFieldName = cursor.getString(FOREIGN_KEY_TO_FIELD);

                SqlDatabaseScheme.TableScheme foreignTable = getTableScheme(tableSchemeMap, foreignTableName);
                SqlDatabaseScheme.FieldScheme toField = foreignTable.getField(toFieldName);

                foreignFields.put(fromFieldName, toField);

                cursor.moveToNext();
            }

            return foreignFields;
        } finally {
            cursor.close();
        }
    }

    @Override
    public SqlDatabaseScheme getDatabaseScheme() {
        return new SimpleSqlDatabaseScheme(database.getPath(), getTables());
    }
}
