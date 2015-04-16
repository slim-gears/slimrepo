// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.core;

import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimorm.internal.interfaces.TransactionProvider;
import com.slimgears.slimorm.internal.sql.AbstractSqlSessionServiceProvider;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlOrmServiceProvider;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public class SqliteSessionServiceProvider extends AbstractSqlSessionServiceProvider {
    private final SQLiteDatabase mSqliteDatabase;

    public SqliteSessionServiceProvider(SqlOrmServiceProvider serviceProvider, SQLiteDatabase sqliteDatabase) {
        super(serviceProvider);
        mSqliteDatabase = sqliteDatabase;
    }

    @Override
    protected SqlCommandExecutor createCommandExecutor() {
        return new SqliteCommandExecutor(mSqliteDatabase);
    }

    @Override
    protected TransactionProvider createTransactionProvider() {
        return new SqliteTransactionProvider(mSqliteDatabase);
    }

}
