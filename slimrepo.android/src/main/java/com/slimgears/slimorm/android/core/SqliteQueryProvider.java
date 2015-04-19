// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.internal.interfaces.EntityCache;
import com.slimgears.slimorm.core.internal.query.PreparedQuery;
import com.slimgears.slimorm.core.internal.sql.SqlQueryProvider;
import com.slimgears.slimorm.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimorm.core.internal.sql.SqlSessionServiceProvider;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public class SqliteQueryProvider<TKey, TEntity extends Entity<TKey>> extends SqlQueryProvider<TKey, TEntity> {
    private final SQLiteDatabase database;
    private final Class keyType;
    private final SqlSessionEntityServiceProvider<TKey, TEntity> entityServiceProvider;

    class ContentValuesAdapter implements FieldValueMap<TEntity> {
        private final ContentValues values;

        ContentValuesAdapter(ContentValues values) {
            this.values = values;
        }

        @Override
        public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
            if (field == entityType.getKeyField()) return this;
            String fieldName = field.metaInfo().getName();
            if (value == null) values.putNull(fieldName);
            else if (value instanceof Integer) values.put(fieldName, (Integer)value);
            else if (value instanceof Long) values.put(fieldName, (Long)value);
            else if (value instanceof Short) values.put(fieldName, (Short)value);
            else if (value instanceof String) values.put(fieldName, (String)value);
            else if (value instanceof Double) values.put(fieldName, (Double)value);
            else if (value instanceof Float) values.put(fieldName, (Float)value);
            else if (value instanceof Boolean) values.put(fieldName, (Boolean)value);
            else if (value instanceof byte[]) values.put(fieldName, (byte[])value);
            else throw new RuntimeException("Not supported value type: " + value.getClass().getSimpleName());
            return this;
        }

        @Override
        public <T> T getValue(Field<TEntity, T> field) {
            return null;
        }
    }

    public SqliteQueryProvider(SQLiteDatabase database, SqlSessionServiceProvider serviceProvider, SqliteSessionEntityServiceProvider<TKey, TEntity> entityServiceProvider, EntityType<TKey, TEntity> entityType) {
        super(serviceProvider, entityType);
        this.database = database;
        this.keyType = entityType.getKeyField().metaInfo().getType();
        this.entityServiceProvider = entityServiceProvider;
    }

    @Override
    public PreparedQuery<Void> prepareInsert(final Collection<TEntity> entitites) {
        return new PreparedQuery<Void>() {
            @SuppressWarnings("unchecked")
            @Override
            public Void execute() throws IOException {
                String tableName = serviceProvider.getSyntaxProvider().tableName(entityType);
                EntityCache<TKey, TEntity> cache = entityServiceProvider.getEntityCache();
                for (TEntity entity : entitites) {
                    long id = insertEntity(tableName, entity);
                    if (keyType == Integer.class) entityType.setKey(entity, (TKey)(Integer)(int)id);
                    else if (keyType == Long.class) entityType.setKey(entity, (TKey)(Long)id);
                    cache.put(entity);
                }
                return null;
            }
        };
    }

    private long insertEntity(String tableName, TEntity entity) {
        return database.insert(tableName, null, entityToContentValues(entity));
    }

    private ContentValues entityToContentValues(TEntity entity) {
        ContentValues values = new ContentValues();
        entityType.entityToMap(entity, new ContentValuesAdapter(values));
        return values;
    }
}
