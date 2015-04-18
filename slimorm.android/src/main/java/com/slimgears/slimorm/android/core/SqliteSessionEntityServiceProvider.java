// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.core;

import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.internal.query.QueryProvider;
import com.slimgears.slimorm.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimorm.core.internal.sql.SqlSessionServiceProvider;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public class SqliteSessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>> extends SqlSessionEntityServiceProvider<TKey, TEntity> {
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
