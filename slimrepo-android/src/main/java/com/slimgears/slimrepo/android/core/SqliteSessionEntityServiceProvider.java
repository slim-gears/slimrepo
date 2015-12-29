// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public class SqliteSessionEntityServiceProvider<TKey, TEntity> extends SqlSessionEntityServiceProvider<TKey, TEntity> {
    private final SQLiteDatabase database;

    public SqliteSessionEntityServiceProvider(SQLiteDatabase database, SqlSessionServiceProvider serviceProvider, EntityType<TKey, TEntity> entityType) {
        super(serviceProvider, entityType);
        this.database = database;
    }

    @Override
    protected QueryProvider<TKey, TEntity> createQueryProvider() {
        return new SqliteQueryProvider<>(database, serviceProvider, this, entityType);
    }
}
