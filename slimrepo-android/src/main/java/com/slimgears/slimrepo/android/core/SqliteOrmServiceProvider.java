// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public class SqliteOrmServiceProvider extends AbstractSqliteOrmServiceProvider {
    private final Context context;
    private final static String TAG = SqliteOrmServiceProvider.class.getSimpleName();

    public SqliteOrmServiceProvider(Context mContext) {
        this.context = mContext;
    }

    class OrmHelper extends SQLiteOpenHelper {
        private final RepositoryModel model;

        public OrmHelper(RepositoryModel model) {
            super(context, getSyntaxProvider().databaseName(model), null, model.getVersion());
            this.model = model;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, String.format("Creating database [version %d]: %s", model.getVersion(), db.getPath()));
            SessionServiceProvider serviceProvider = createSessionServiceProvider(db, null);
            try {
                serviceProvider
                        .getRepositoryCreator()
                        .createRepository(model);
                serviceProvider.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, String.format("Upgrading database [version %d --> %d]: %s", oldVersion, newVersion, db.getPath()));
            SessionServiceProvider serviceProvider = createSessionServiceProvider(db, null);
            try {
                serviceProvider
                        .getRepositoryCreator()
                        .upgradeRepository(model);
                serviceProvider.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public SessionServiceProvider createSessionServiceProvider(RepositoryModel model) {
        final OrmHelper helper = new OrmHelper(model);
        SQLiteDatabase db = helper.getWritableDatabase();
        return createSessionServiceProvider(db, new Closeable() {
            @Override
            public void close() throws IOException {
                helper.close();
            }
        });
    }

    protected SessionServiceProvider createSessionServiceProvider(SQLiteDatabase database, Closeable closer) {
        return new SqliteSessionServiceProvider(this, database, closer);
    }

    @Override
    protected void onMapFieldTypes(FieldTypeMappingRegistrar registrar) {
        installTypeMappings(registrar, new ParcelableTypeMappingInstaller());
        super.onMapFieldTypes(registrar);
    }
}
