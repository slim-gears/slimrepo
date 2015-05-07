// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.EntityCache;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.query.PreparedQuery;
import com.slimgears.slimrepo.core.internal.sql.SqlQueryProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public class SqliteQueryProvider<TKey, TEntity extends Entity<TKey>> extends SqlQueryProvider<TKey, TEntity> {
    private final SQLiteDatabase database;
    private final Class keyType;
    private final FieldTypeMapper fieldTypeMapper;
    private final SqlSessionEntityServiceProvider<TKey, TEntity> entityServiceProvider;

    class ContentValuesAdapter implements FieldValueMap<TEntity> {
        private final ContentValues values;

        ContentValuesAdapter(ContentValues values) {
            this.values = values;
        }

        @Override
        public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
            if (field == entityType.getKeyField()) return this;

            Object dbValue = fieldTypeMapper.fromFieldType(field, value);
            String fieldName = field.metaInfo().getName();

            if (dbValue == null) values.putNull(fieldName);
            else if (dbValue instanceof Integer) values.put(fieldName, (Integer)dbValue);
            else if (dbValue instanceof Long) values.put(fieldName, (Long)dbValue);
            else if (dbValue instanceof Short) values.put(fieldName, (Short)dbValue);
            else if (dbValue instanceof String) values.put(fieldName, (String)dbValue);
            else if (dbValue instanceof Double) values.put(fieldName, (Double)dbValue);
            else if (dbValue instanceof Float) values.put(fieldName, (Float)dbValue);
            else if (dbValue instanceof Boolean) values.put(fieldName, (Boolean)dbValue);
            else if (dbValue instanceof byte[]) values.put(fieldName, (byte[])dbValue);
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
        this.keyType = entityType.getKeyField().metaInfo().getValueType();
        this.entityServiceProvider = entityServiceProvider;
        this.fieldTypeMapper = serviceProvider.getOrmServiceProvider().getFieldTypeMapper();
    }

    @Override
    public PreparedQuery<Void> prepareInsert(final Collection<TEntity> entitites) {
        return new PreparedQuery<Void>() {
            @SuppressWarnings("unchecked")
            @Override
            public Void execute() throws IOException {
                String tableName = serviceProvider.getOrmServiceProvider().getSyntaxProvider().tableName(entityType);
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
