package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.IOException;

/**
 * Created by Denis on 02-May-15.
 */
public abstract class AbstractRowIterator<T, TKey, TEntity> implements CloseableIterator<T> {
    private final CloseableIterator<FieldValueLookup<TEntity>> rowIterator;

    public AbstractRowIterator(CloseableIterator<FieldValueLookup<TEntity>> rowIterator) {
        this.rowIterator = rowIterator;
    }

    @Override
    public void close() throws IOException {
        rowIterator.close();
    }

    @Override
    public boolean hasNext() {
        return rowIterator.hasNext();
    }

    @Override
    public T next() {
        return toElement(rowIterator.next());
    }

    @Override
    public void remove() {
        rowIterator.remove();
    }

    protected abstract T toElement(FieldValueLookup<TEntity> lookup);
}
