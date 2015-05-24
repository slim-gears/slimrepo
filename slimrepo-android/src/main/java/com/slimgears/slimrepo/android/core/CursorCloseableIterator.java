package com.slimgears.slimrepo.android.core;

import android.database.Cursor;

import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.IOException;

/**
 * Created by Denis on 19-May-15.
 */
public abstract class CursorCloseableIterator<T> implements CloseableIterator<T> {
    protected final Cursor cursor;
    private boolean needsMove = false;

    protected CursorCloseableIterator(Cursor cursor) {
        this.cursor = cursor;
        cursor.moveToFirst();
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }

    @Override
    public boolean hasNext() {
        return !((needsMove && cursor.isLast()) || cursor.isAfterLast());
    }

    @Override
    public T next() {
        if (needsMove) cursor.moveToNext();
        needsMove = true;
        return getItem(cursor);
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not implemented");
    }

    protected abstract T getItem(Cursor cursor);
}
