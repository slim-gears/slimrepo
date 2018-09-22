// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.database.sqlite.SQLiteDatabase;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;

import java.io.IOException;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class SqliteTransactionProvider implements TransactionProvider {
    private final SQLiteDatabase mSqliteDatabase;

    public SqliteTransactionProvider(SQLiteDatabase mSqliteDatabase) {
        this.mSqliteDatabase = mSqliteDatabase;
    }

    @Override
    public void beginTransaction() throws IOException {
        mSqliteDatabase.beginTransaction();
    }

    @Override
    public void cancelTransaction() throws IOException {
        mSqliteDatabase.endTransaction();
    }

    @Override
    public void commitTransaction() throws IOException {
        mSqliteDatabase.setTransactionSuccessful();
        mSqliteDatabase.endTransaction();
    }

}
