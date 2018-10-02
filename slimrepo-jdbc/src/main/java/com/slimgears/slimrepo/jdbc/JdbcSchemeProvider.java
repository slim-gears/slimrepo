package com.slimgears.slimrepo.jdbc;

import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.SimpleSqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.slimgears.slimrepo.jdbc.JdbcHelper.*;

/**
 * Created by Denis on 21-May-15.
 *
 */
public class JdbcSchemeProvider extends AbstractSqlSchemeProvider {
    private final static int DB_SCHEME_TABLE_CATALOG = 1;
    private final static int DB_SCHEME_TABLE_NAME = 3;

    private final static int TABLE_SCHEME_FIELD_NAME = 4;
    private final static int TABLE_SCHEME_FIELD_TYPE = 6;
    private final static int TABLE_SCHEME_FIELD_NULLABLE = 11;
    private final static int TABLE_SCHEME_FIELD_DEF_VALUE = 13;
    private final static int PRIMARY_KEYS_COLUMN_NAME = 4;

    private final static int FOREIGN_KEY_PK_CATALOG = 1;
    private final static int FOREIGN_KEY_PK_TABLE = 3;
    private final static int FOREIGN_KEY_PK_COLUMN = 4;
    private final static int FOREIGN_KEY_FK_COLUMN = 8;

    private final DatabaseMetaData metaData;
    private final Map<String, SqlDatabaseScheme.TableScheme> tableSchemeMap = new HashMap<>();

//    static {
//        DEFAULT_VALUES.put("INTEGER", 0);
//        DEFAULT_VALUES.put("REAL", 0.0);
//        DEFAULT_VALUES.put("TEXT", "''");
//    }

    public JdbcSchemeProvider(SqlStatementBuilder.SyntaxProvider syntaxProvider, Connection connection) {
        super(syntaxProvider);
        this.metaData = JdbcHelper.execute(connection::getMetaData);
    }

    private SqlDatabaseScheme.TableScheme[] getTables(String catalog) throws SQLException {
        return toStream(metaData.getTables(catalog, null, null, null))
                .map(fromFunction(this::toTableScheme))
                .toArray(SqlDatabaseScheme.TableScheme[]::new);
    }

    private SqlDatabaseScheme.TableScheme toTableScheme(ResultSet resultSet) throws SQLException {
        String catalog = resultSet.getString(DB_SCHEME_TABLE_CATALOG);
        String tableName = resultSet.getString(DB_SCHEME_TABLE_NAME);
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
        boolean nullable = resultSet.getInt(TABLE_SCHEME_FIELD_NULLABLE) == 1;
        boolean isPrimaryKey = fieldName.equals(keyColumn);
        return new SimpleSqlDatabaseScheme.SimpleFieldScheme(
                tableScheme,
                fieldName,
                fieldType,
                nullable,
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
                        fromFunction(rs -> rs.getString(FOREIGN_KEY_FK_COLUMN)),
                        fromFunction(rs -> {
                            String foreignCatalog = rs.getString(FOREIGN_KEY_PK_CATALOG);
                            String foreignTable = rs.getString(FOREIGN_KEY_PK_TABLE);
                            String foreignColumn = rs.getString(FOREIGN_KEY_PK_COLUMN);
                            return getFieldScheme(foreignCatalog, foreignTable, foreignColumn);
                        })));

        toStream(metaData.getColumns(catalogName, null, tableName, null))
                .map(fromFunction(rs -> toFieldScheme(tableScheme, rs, keyColumn, foreignKeys)))
                .forEach(tableScheme::addField);

        return tableScheme;
    }

    @Override
    public SqlDatabaseScheme getDatabaseScheme(String catalog) {
        return new SimpleSqlDatabaseScheme(catalog, execute(() -> getTables(catalog)));
    }

    private String fullTableName(String catalog, String table) {
        return catalog == null || catalog.isEmpty() ? table : catalog + '.' + table;
    }
}
