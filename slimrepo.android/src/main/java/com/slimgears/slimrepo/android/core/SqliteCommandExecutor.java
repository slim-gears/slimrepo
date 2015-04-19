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
import com.slimgears.slimrepo.core.internal.sql.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.SqlCommandExecutor;
import com.slimgears.slimrepo.core.internal.sql.SqlSessionServiceProvider;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 10-Apr-15
 * <File Description>
 */
public class SqliteCommandExecutor implements SqlCommandExecutor {
    private final SQLiteDatabase database;

    class CursorIteratorAdapter<T> implements CloseableIterator<FieldValueLookup<T>> {
        private final Cursor cursor;
        private Map<Field, Integer> fieldToIndexMap = new HashMap<>();
        private Map<Field, Class> fieldToTypeMap = new HashMap<>();
        private final Lookup lookup = new Lookup();
        private boolean needsMove;

        @Override
        public void close() throws IOException {
            cursor.close();
        }

        class Lookup implements FieldValueLookup<T> {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @SuppressWarnings("unchecked")
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field);
                Class fieldType = getFieldType(field);

                if (fieldType == Integer.class) return (T1)(Integer)cursor.getInt(columnIndex);
                if (fieldType == String.class) return (T1) cursor.getString(columnIndex);
                if (fieldType == Boolean.class) return (T1)(Boolean)(cursor.getShort(columnIndex) != 0);
                if (fieldType == Float.class) return (T1)(Float) cursor.getFloat(columnIndex);
                if (fieldType == Byte.class) return (T1)(Byte)(byte) cursor.getShort(columnIndex);
                if (fieldType == Date.class) return (T1)new Date(cursor.getLong(columnIndex));
                if (fieldType == Short.class) return (T1)(Short) cursor.getShort(columnIndex);
                if (fieldType == Long.class) return (T1)(Long) cursor.getLong(columnIndex);
                if (fieldType == Double.class) return (T1)(Double) cursor.getDouble(columnIndex);

                return null;
            }
        }

        private Class getFieldType(Field field) {
            Class type;
            if (fieldToTypeMap != null) {
                type = fieldToTypeMap.get(field);
                if (type != null) return type;
            } else {
                fieldToTypeMap = new HashMap<>();
            }

            type = field.metaInfo().getType();
            fieldToTypeMap.put(field, type);
            return type;
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
    }

    @Override
    public long count(SqlCommand command) throws IOException {
        return DatabaseUtils.longForQuery(database, command.getStatement(), toStringArguments(command.getParameters()));
    }

    @Override
    public <T> CloseableIterator<FieldValueLookup<T>> select(final SqlCommand command) throws IOException {
        String sql = command.getStatement();
        String[] params = toStringArguments(command.getParameters());
        @SuppressLint("Recycle") Cursor cursor = database.rawQuery(sql, params);
        return new CursorIteratorAdapter<>(cursor);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void execute(SqlCommand command) throws IOException {
        String sql = command.getStatement();
        String[] params = toStringArguments(command.getParameters());
        database.execSQL(sql, params);
    }

    private String[] toStringArguments(SqlCommand.Parameters params) {
        Object[] values = params.getValues();
        String[] strValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            strValues[i] = String.valueOf(values[i]);
        }
        return strValues;
    }
}
