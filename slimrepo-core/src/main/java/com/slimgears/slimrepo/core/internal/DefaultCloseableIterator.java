package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Denis on 01-May-15.
 */
public class DefaultCloseableIterator<T> implements CloseableIterator<T> {
    private final Closeable[] closeables;
    private final Iterator<T> iterator;

    public DefaultCloseableIterator(Iterator<T> iterator, Closeable... closeables) {
        this.closeables = closeables;
        this.iterator = iterator;
    }

    @Override
    public void close() throws IOException {
        for (Closeable closeable : closeables) {
            closeable.close();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
