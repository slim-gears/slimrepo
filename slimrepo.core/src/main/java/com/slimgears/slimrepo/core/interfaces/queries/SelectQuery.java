package com.slimgears.slimrepo.core.interfaces.queries;

import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.IOException;
import java.util.List;

/**
 * Created by Denis on 02-May-15.
 */
public interface SelectQuery<T> {
    T firstOrDefault() throws IOException;
    List<T> toList() throws IOException;
    T[] toArray() throws IOException;
    long count() throws IOException;
    CloseableIterator<T> iterator();
}
