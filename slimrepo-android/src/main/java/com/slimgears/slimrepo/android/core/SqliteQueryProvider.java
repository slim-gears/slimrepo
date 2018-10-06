// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterators;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.query.PreparedQuery;
import com.slimgears.slimrepo.core.internal.sql.SqlQueryProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;

import java.util.Collection;

/**
 * Created by Denis on 18-Apr-15
 *
 */
public class SqliteQueryProvider<TKey, TEntity> extends SqlQueryProvider<TKey, TEntity> {
    private final SQLiteDatabase database;
    private final Class<TKey> keyType;
    private final FieldTypeMapper fieldTypeMapper;
    private final SqlSessionEntityServiceProvider<TKey, TEntity> entityServiceProvider;

    class ContentValuesAdapter implements FieldValueMap<TEntity> {
        private final ContentValues values;

        ContentValuesAdapter(ContentValues values) {
            this.values = values;
        }

        @Override
        public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
            if (field.metaInfo().isAutoIncremented()) {
                return this;
            }

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

    @SuppressWarnings("unchecked")
    @Override
    public PreparedQuery<CloseableIterator<TKey>> prepareInsert(final Collection<TEntity> entities) {
        return () -> {
            String tableName = serviceProvider.getOrmServiceProvider().getSyntaxProvider().tableName(entityType);
            return CloseableIterators
                    .fromIterator(Stream.of(entities)
                            .map(entity -> insertEntity(tableName, entity))
                            .iterator());
        };
    }

    private TKey insertEntity(String tableName, TEntity entity) {
        long key = database.insert(tableName, null, entityToContentValues(entity));
        if (keyType == Integer.class) {
            //noinspection unchecked
            return (TKey)(Integer)(int)key;
        } else if (keyType == Long.class) {
            //noinspection unchecked
            return (TKey)(Object)key;
        } else {
            return entityType.getKey(entity);
        }
    }

    private ContentValues entityToContentValues(TEntity entity) {
        ContentValues values = new ContentValues();
        entityType.entityToMap(entity, new ContentValuesAdapter(values));
        return values;
    }
}
