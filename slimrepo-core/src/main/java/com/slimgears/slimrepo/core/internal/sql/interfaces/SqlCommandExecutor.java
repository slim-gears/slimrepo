// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

/**
 * Created by Denis on 08-Apr-15
 *
 */
public interface SqlCommandExecutor {
    long count(String statement, String... parameters) throws Exception;
    <T> CloseableIterator<FieldValueLookup<T>> select(String statement, String... parameters) throws Exception;
    <K> CloseableIterator<K> insert(String statement, String... parameters) throws Exception;
    void execute(String statement, String... parameters) throws Exception;
}
