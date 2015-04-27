// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.sql.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlStatementBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 10-Apr-15
 * <File Description>
 */
public class SqliteCommandExecutor implements SqlCommandExecutor {
    private final SQLiteDatabase database;
    private final FieldTypeMapper fieldTypeMapper;

    class CursorIteratorAdapter<T> implements CloseableIterator<FieldValueLookup<T>> {
        private final Cursor cursor;
        private Map<Field, Integer> fieldToIndexMap = new HashMap<>();
        private final Lookup lookup = new Lookup();
        private boolean needsMove;

        @Override
        public void close() throws IOException {
            cursor.close();
        }

        class Lookup implements FieldValueLookup<T> {
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field);
                Class dbType = fieldTypeMapper.getMappedType(field);
                Object value = getValue(dbType, cursor, columnIndex);
                return fieldTypeMapper.toFieldType(field, value);
            }

            private Object getValue(Class type, Cursor cursor, int columnIndex) {
                if (type == Integer.class) return cursor.getInt(columnIndex);
                if (type == String.class) return cursor.getString(columnIndex);
                if (type == Boolean.class) return cursor.getShort(columnIndex) != 0;
                if (type == Float.class) return cursor.getFloat(columnIndex);
                if (type == Byte.class) return cursor.getShort(columnIndex);
                if (type == Short.class) return cursor.getShort(columnIndex);
                if (type == Long.class) return cursor.getLong(columnIndex);
                if (type == Double.class) return cursor.getDouble(columnIndex);
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

            index = cursor.getColumnIndex(field.metaInfo().getName());
            fieldToIndexMap.put(field, index);
            return index;
        }

        public CursorIteratorAdapter(Cursor cursor) {
            cursor.moveToFirst();
            needsMove = false;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return !((needsMove && cursor.isLast()) || cursor.isAfterLast());
        }

        @Override
        public FieldValueLookup<T> next() {
            if (needsMove) cursor.moveToNext();
            needsMove = true;
            return lookup;
        }

        @Override
        public void remove() {

        }
    }

    public SqliteCommandExecutor(SQLiteDatabase sqliteDatabase, SqlSessionServiceProvider sessionServiceProvider) {
        database = sqliteDatabase;
        fieldTypeMapper = sessionServiceProvider.getOrmServiceProvider().getFieldTypeMapper();
    }

    @Override
    public long count(SqlCommand command) throws IOException {
        return DatabaseUtils.longForQuery(database, command.getStatement(), command.getParameters().getValues());
    }

    @Override
    public <T> CloseableIterator<FieldValueLookup<T>> select(final SqlCommand command) throws IOException {
        String sql = command.getStatement();
        String[] params = command.getParameters().getValues();
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(sql, params);
        return new CursorIteratorAdapter<>(cursor);
    }

    @Override
    public void execute(SqlCommand command) throws IOException {
        String sql = command.getStatement();
        String[] params = command.getParameters().getValues();
        database.execSQL(sql, params);
    }
}
