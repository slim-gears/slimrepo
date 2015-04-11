// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.core;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.CloseableIterator;
import com.slimgears.slimorm.internal.sql.SqlCommand;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 10-Apr-15
 * <File Description>
 */
public class SqliteCommandExecutor implements SqlCommandExecutor {
    private final SQLiteDatabase mSqliteDatabase;

    class CursorIteratorAdapter<T> implements CloseableIterator<FieldValueLookup<T>> {
        private final Cursor mCursor;
        private final Map<String, Integer> mIndexes = new HashMap<>();
        private final Lookup mLookup = new Lookup();

        @Override
        public void close() throws IOException {
            mCursor.close();
        }

        class Lookup implements FieldValueLookup<T> {
            @SuppressWarnings("unchecked")
            @Override
            public <T1> T1 getValue(Field<T, T1> field) {
                int columnIndex = getColumnIndex(field.getName());
                Class fieldType = field.getType();

                if (fieldType == Integer.class) return (T1)(Integer)mCursor.getInt(columnIndex);
                if (fieldType == Short.class) return (T1)(Short)mCursor.getShort(columnIndex);
                if (fieldType == Long.class) return (T1)(Long)mCursor.getLong(columnIndex);
                if (fieldType == Float.class) return (T1)(Float)mCursor.getFloat(columnIndex);
                if (fieldType == Double.class) return (T1)(Double)mCursor.getDouble(columnIndex);
                if (fieldType == String.class) return (T1)mCursor.getString(columnIndex);

                return null;
            }
        }

        private int getColumnIndex(String name) {
            Integer index = mIndexes.get(name);
            if (index != null) return index;
            return mIndexes.put(name, mCursor.getColumnIndex(name));
        }

        public CursorIteratorAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return mCursor.isBeforeFirst();
        }

        @Override
        public FieldValueLookup<T> next() {
            mCursor.moveToNext();
            return mLookup;
        }

        @Override
        public void remove() {

        }
    }
    public SqliteCommandExecutor(SQLiteDatabase sqliteDatabase) {
        mSqliteDatabase = sqliteDatabase;
    }

    @Override
    public int count(SqlCommand command) throws IOException {
        return (int)DatabaseUtils.longForQuery(mSqliteDatabase, command.getStatement(), toStringArguments(command.getParameters()));
    }

    @Override
    public <T> CloseableIterator<FieldValueLookup<T>> select(final SqlCommand command) throws IOException {
        @SuppressLint("Recycle") Cursor cursor = mSqliteDatabase.rawQuery(command.getStatement(), toStringArguments(command.getParameters()));
        return new CursorIteratorAdapter<>(cursor);
    }

    @Override
    public void execute(SqlCommand command) throws IOException {
        mSqliteDatabase.execSQL(command.getStatement(), toStringArguments(command.getParameters()));
    }

    @Override
    public void beginTransaction() throws IOException {
        mSqliteDatabase.beginTransaction();
    }

    @Override
    public void cancelTransaction() throws IOException {
        mSqliteDatabase.endTransaction();
    }

    @Override
    public void commitTransaction() throws IOException {
        mSqliteDatabase.setTransactionSuccessful();
        mSqliteDatabase.endTransaction();
    }

    @Override
    public void close() throws IOException {
        mSqliteDatabase.close();
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
