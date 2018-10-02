package com.slimgears.slimrepo.sqlite;

import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.SimpleSqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.slimgears.slimrepo.sqlite.JdbcHelper.*;

/**
 * Created by Denis on 21-May-15.
 *
 */
public class JdbcSchemeProvider extends AbstractSqlSchemeProvider {
    //private final static Set<String> IGNORED_TABLES = new HashSet<>(Collections.singletonList("android_metadata"));
    //private final static Map<String, Object> DEFAULT_VALUES = new HashMap<>();

    private final static int TABLE_SCHEME_FIELD_NAME = 4;
    private final static int TABLE_SCHEME_FIELD_TYPE = 6;
    private final static int TABLE_SCHEME_FIELD_NULLABLE = 11;
    private final static int TABLE_SCHEME_FIELD_DEF_VALUE = 13;
    private final static int PRIMARY_KEYS_COLUMN_NAME = 4;

    private final static int FOREIGN_KEY_TABLE_NAME = 2;
    private final static int FOREIGN_KEY_FROM_FIELD = 3;
    private final static int FOREIGN_KEY_TO_FIELD = 4;

    private final Connection connection;
    private final DatabaseMetaData metaData;
    private final Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap = new HashMap<>();

//    static {
//        DEFAULT_VALUES.put("INTEGER", 0);
//        DEFAULT_VALUES.put("REAL", 0.0);
//        DEFAULT_VALUES.put("TEXT", "''");
//    }

    public JdbcSchemeProvider(SqlStatementBuilder.SyntaxProvider syntaxProvider, Connection connection) {
        super(syntaxProvider);
        this.connection = connection;
        this.metaData = JdbcHelper.execute(connection::getMetaData);
    }

    private SqlDatabaseScheme.TableScheme[] getTables() throws SQLException {
        return toStream(metaData.getTables(null, null, null, null))
                .map(fromFunction(this::toTableScheme))
                .toArray(SqlDatabaseScheme.TableScheme[]::new);
    }

    private SqlDatabaseScheme.TableScheme toTableScheme(ResultSet resultSet) throws SQLException {
        String catalog = resultSet.getString(1);
        String tableName = resultSet.getString(3);
        return getTableScheme(catalog, tableName);
    }

    private SqlDatabaseScheme.TableScheme getTableScheme(String catalog, String table) {
        return tableSchemeMap.computeIfAbsent(fullTableName(catalog, table), fromFunction(key -> createTableScheme(catalog, table)));
    }

    private SqlDatabaseScheme.FieldScheme getFieldScheme(String catalog, String table, String column) {
        return getTableScheme(catalog, table).getField(column);
    }

    private SqlDatabaseScheme.FieldScheme toFieldScheme(SqlDatabaseScheme.TableScheme tableScheme, ResultSet resultSet, String keyColumn, Map<String, SqlDatabaseScheme.FieldScheme> foreignKeys) throws SQLException {
        String fieldName = resultSet.getString(TABLE_SCHEME_FIELD_NAME);
        String fieldType = resultSet.getString(TABLE_SCHEME_FIELD_TYPE);
        String defValue = resultSet.getString(TABLE_SCHEME_FIELD_DEF_VALUE);
        boolean notNull = resultSet.getInt(TABLE_SCHEME_FIELD_NULLABLE) != 0;
        boolean isPrimaryKey = fieldName.equals(keyColumn);
        return new SimpleSqlDatabaseScheme.SimpleFieldScheme(
                tableScheme,
                fieldName,
                fieldType,
                notNull,
                isPrimaryKey,
                foreignKeys.get(fieldName),
                defValue);
    }

    private String keyColumnName(String catalogName, String tableName) throws SQLException {
        return toStream(metaData.getPrimaryKeys(catalogName, null, tableName))
                .map(fromFunction(rs -> rs.getString(PRIMARY_KEYS_COLUMN_NAME)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find primary key for table " + catalogName + "." + tableName));
    }

    private SqlDatabaseScheme.TableScheme createTableScheme(String catalogName, String tableName) throws SQLException {
        SimpleSqlDatabaseScheme.SimpleTableScheme tableScheme = new SimpleSqlDatabaseScheme.SimpleTableScheme(catalogName, tableName);
        String keyColumn = keyColumnName(catalogName, tableName);
        Map<String, SqlDatabaseScheme.FieldScheme> foreignKeys =
                toStream(metaData.getImportedKeys(catalogName, null, tableName))
                .collect(Collectors.toMap(
                        fromFunction(rs -> rs.getString(4)),
                        fromFunction(rs -> {
                            String foreignCatalog = rs.getString(5);
                            String foreignTable = rs.getString(7);
                            String foreignColumn = rs.getString(8);
                            return getFieldScheme(foreignCatalog, foreignTable, foreignColumn);
                        })));

        toStream(metaData.getColumns(catalogName, null, tableName, null))
                .map(fromFunction(rs -> toFieldScheme(tableScheme, rs, keyColumn, foreignKeys)))
                .forEach(tableScheme::addField);
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
        String dbName = execute(() -> connection.getMetaData().getDatabaseProductName());
        return new SimpleSqlDatabaseScheme(dbName, getTables());
    }

    private String fullTableName(String catalog, String table) {
        return catalog == null || catalog.isEmpty() ? table : catalog + '.' + table;
    }
}
