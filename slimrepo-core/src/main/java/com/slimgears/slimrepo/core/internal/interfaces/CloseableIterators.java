package com.slimgears.slimrepo.core.internal.interfaces;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class CloseableIterators {
    public static <T> CloseableIterator<T> fromIterator(Iterator<? extends T> iterator) {
        return fromIterator(iterator, () -> {});
    }

    public static <T> CloseableIterator<T> empty() {
        return new CloseableIterator<T>() {
            @Override
            public void close() throws IOException {

            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        };
    }

    public static <T> CloseableIterator<T> addCloseable(CloseableIterator<T> iterator, Closeable closeable) {
        return new CloseableIterator<T>() {
            @Override
            public void close() throws IOException {
                iterator.close();
                closeable.close();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    public static <T> CloseableIterator<T> fromIterator(Iterator<? extends T> iterator, Closeable closeable) {
        return new CloseableIterator<T>() {
            @Override
            public void close() throws IOException {
                closeable.close();
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }
}
