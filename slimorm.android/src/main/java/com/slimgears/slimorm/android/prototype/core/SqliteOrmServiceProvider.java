// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.slimgears.slimorm.internal.interfaces.RepositoryModel;
import com.slimgears.slimorm.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimorm.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import java.io.IOException;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public class SqliteOrmServiceProvider extends AbstractSqliteOrmServiceProvider {
    private final Context context;

    public SqliteOrmServiceProvider(Context mContext) {
        this.context = mContext;
    }

    class OrmHelper extends SQLiteOpenHelper {
        private final RepositoryModel model;

        public OrmHelper(RepositoryModel model) {
            super(context, model.getName(), null, model.getVersion());
            this.model = model;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                createSessionServiceProvider(db)
                        .getRepositoryCreator()
                        .createRepository(model);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    @Override
    public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        OrmHelper helper = new OrmHelper(model);
        SQLiteDatabase db = helper.getWritableDatabase();
        return createSessionServiceProvider(db);
    }

    protected SessionServiceProvider createSessionServiceProvider(SQLiteDatabase database) {
        return new SqliteSessionServiceProvider(this, database);
    }
}
