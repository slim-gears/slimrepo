package com.slimgears.slimrepo.core.interfaces.queries;

import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.util.List;

/**
 * Created by Denis on 02-May-15.
 */
public interface SelectQuery<T> {
    T firstOrDefault() throws Exception;
    List<T> toList() throws Exception;
    T[] toArray() throws Exception;
    long count() throws Exception;
    CloseableIterator<T> iterator();
}
