// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.interfaces.AbstractRepositoryCreator;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.*;
import com.slimgears.slimrepo.core.utilities.Sets;

import java.util.Set;

/**
 * Created by Denis on 15-Apr-15
 *
 */
class SqlRepositoryCreator extends AbstractRepositoryCreator {
    private final TransactionProvider transactionProvider;
    private final SqlCommandExecutor sqlExecutor;
    private final SqlStatementBuilder sqlBuilder;
    private final SqlSchemeProvider schemeProvider;

    public SqlRepositoryCreator(SqlSessionServiceProvider sessionServiceProvider) {
        this.transactionProvider = sessionServiceProvider.getTransactionProvider();
        this.sqlBuilder = sessionServiceProvider.getOrmServiceProvider().getStatementBuilder();
        this.sqlExecutor = sessionServiceProvider.getExecutor();
        this.schemeProvider = sessionServiceProvider.getSchemeProvider();
    }

    @Override
    public void createRepository(RepositoryModel model) throws Exception {
        transactionProvider.beginTransaction();
        try {
            SqlDatabaseScheme scheme = schemeProvider.getModelScheme(model);
            createScheme(scheme);
        } catch (Throwable e) {
            transactionProvider.cancelTransaction();
            throw e;
        }
        transactionProvider.commitTransaction();
    }

    private void createScheme(SqlDatabaseScheme scheme) throws Exception {
        for (SqlDatabaseScheme.TableScheme table : scheme.getTables().values()) {
            createTable(table);
        }
    }

    @Override
    public void upgradeRepository(RepositoryModel newModel) throws Exception {
        SqlDatabaseScheme actualScheme = schemeProvider.getDatabaseScheme(newModel.getName());
        SqlDatabaseScheme modelScheme = schemeProvider.getModelScheme(newModel);
        SqlDatabaseSchemeDifference diff = SqlDatabaseSchemes.compareDatabases(actualScheme, modelScheme);

        transactionProvider.beginTransaction();
        try {
            for (String tableName : diff.getDeletedTables().keySet()) {
                dropTable(tableName);
            }

            for (SqlDatabaseScheme.TableScheme table : diff.getAddedTables().values()) {
                createTable(table);
            }

            for (SqlDatabaseSchemeDifference.TableSchemeDifference tableDiff : diff.getModifiedTables().values()) {
                upgradeTable(tableDiff);
            }
        } catch (Throwable e) {
            transactionProvider.cancelTransaction();
            throw e;
        }
        transactionProvider.commitTransaction();
    }

    private void createTable(SqlDatabaseScheme.TableScheme tableScheme) throws Exception {
        sqlExecutor.execute(sqlBuilder.createTableStatement(tableScheme));
    }

    private void dropTable(String tableName) throws Exception {
        sqlExecutor.execute(sqlBuilder.dropTableStatement(tableName));
    }

    private void upgradeTable(SqlDatabaseSchemeDifference.TableSchemeDifference tableDiff) throws Exception {
        SqlDatabaseScheme.TableScheme targetTable = tableDiff.getNewTableScheme();
        String targetTableName = targetTable.getName();
        String backupTableName = targetTableName + "_Backup";
        dropTable(backupTableName);
        cloneTable(targetTableName, backupTableName);
        dropTable(targetTableName);
        createTable(tableDiff.getNewTableScheme());

        Set<String> oldFieldNames = tableDiff.getOldTableScheme().getFields().keySet();
        Iterable<String> fieldNames = Sets.difference(oldFieldNames, tableDiff.getDeletedFields().keySet());

        copyData(backupTableName, targetTable, fieldNames);
    }

    private void cloneTable(String srcTableName, String clonedTableName) throws Exception {
        sqlExecutor.execute(sqlBuilder.cloneTableStatement(srcTableName, clonedTableName));
    }

    private void copyData(String fromTable, SqlDatabaseScheme.TableScheme toTable, Iterable<String> fieldNames) throws Exception {
        sqlExecutor.execute(sqlBuilder.copyData(fromTable, toTable, fieldNames));
    }

    @Override
    protected boolean repositoryExists(RepositoryModel model) {
        return !schemeProvider.getDatabaseScheme(model.getName()).getTables().isEmpty();
    }

    @Override
    protected boolean repositoryRequiresUpgrade(RepositoryModel model) {
        SqlDatabaseScheme actualScheme = schemeProvider.getDatabaseScheme(model.getName());
        SqlDatabaseScheme modelScheme = schemeProvider.getModelScheme(model);
        SqlDatabaseSchemeDifference diff = SqlDatabaseSchemes.compareDatabases(actualScheme, modelScheme);
        return !diff.isEmpty();
    }
}
