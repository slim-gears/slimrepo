// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 10-Apr-15
 *
 */
public class SqliteCommandExecutor implements SqlCommandExecutor {
    private final SQLiteDatabase database;
    private final FieldTypeMapper fieldTypeMapper;
    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;

    class CursorIteratorAdapter<T> extends CursorCloseableIterator<FieldValueLookup<T>> {
        private Map<Field, Integer> fieldToIndexMap = new HashMap<>();
        private final Lookup lookup = new Lookup();

        class Lookup implements FieldValueLookup<T> {
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field);

                Class dbType = fieldTypeMapper.getInboundType(field);
                Object value = getValue(dbType, cursor, columnIndex);
                return fieldTypeMapper.toFieldType(field, value);
            }

            private Object getValue(Class type, Cursor cursor, int columnIndex) {
                if (cursor.isNull(columnIndex)) return null;

                if (type == Integer.class) return cursor.getInt(columnIndex);
                if (type == String.class) return cursor.getString(columnIndex);
                if (type == Float.class) return cursor.getFloat(columnIndex);
                if (type == Short.class) return cursor.getShort(columnIndex);
                if (type == Long.class) return cursor.getLong(columnIndex);
                if (type == Double.class) return cursor.getDouble(columnIndex);
                if (type == byte[].class) return cursor.getBlob(columnIndex);
                if (type == FieldValueLookup.class) return this;

                throw new RuntimeException("Unsupported value type: " + type.getSimpleName());
            }
        }

        private int getColumnIndex(Field field) {
            Integer index;
            if (fieldToIndexMap != null) {
                index = fieldToIndexMap.get(field);
                if (index != null) return index;
            } else {
                fieldToIndexMap = new HashMap<>();
            }

            String columnName = syntaxProvider.rawFieldAlias(field);
            index = cursor.getColumnIndex(columnName);
            if (index < 0) throw new RuntimeException("Column '" + columnName + "' not found");

            fieldToIndexMap.put(field, index);
            return index;
        }

        public CursorIteratorAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override
        protected FieldValueLookup<T> getItem(Cursor cursor) {
            return lookup;
        }
    }

    public SqliteCommandExecutor(SQLiteDatabase sqliteDatabase, SqlSessionServiceProvider sessionServiceProvider) {
        database = sqliteDatabase;
        SqlOrmServiceProvider serviceProvider = sessionServiceProvider.getOrmServiceProvider();
        syntaxProvider = serviceProvider.getSyntaxProvider();
        fieldTypeMapper = serviceProvider.getFieldTypeMapper();
    }

    @Override
    public long count(String statement, String... params) throws IOException {
        return DatabaseUtils.longForQuery(database, statement, params);
    }

    @Override
    public <T> CloseableIterator<FieldValueLookup<T>> select(final String statement, final String... params) throws IOException {
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(statement, params);
        return new CursorIteratorAdapter<>(cursor);
    }

    @Override
    public <K> CloseableIterator<K> insert(String statement, String... parameters) throws Exception {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void execute(String statement, String... params) throws IOException {
        database.execSQL(statement, params);
    }
}
