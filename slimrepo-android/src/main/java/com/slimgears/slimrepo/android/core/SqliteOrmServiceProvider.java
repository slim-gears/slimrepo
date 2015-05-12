// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.sqlite.AbstractSqliteOrmServiceProvider;

import java.io.Closeable;
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
            super(context, getSyntaxProvider().databaseName(model), null, model.getVersion());
            this.model = model;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            SessionServiceProvider serviceProvider = createSessionServiceProvider(db, null);
            try {
                serviceProvider
                        .getRepositoryCreator()
                        .createRepository(model);
                serviceProvider.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            SessionServiceProvider serviceProvider = createSessionServiceProvider(db, null);
            try {
                serviceProvider
                        .getRepositoryCreator()
                        .upgradeRepository(model);
                serviceProvider.close();
            } catch (IOException e) {
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
        super.onMapFieldTypes(registrar);
        installTypeMappings(registrar, new ParcelableTypeMappingInstaller());
    }
}
