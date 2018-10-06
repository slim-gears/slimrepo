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
    long count(SqlCommand command) throws Exception;
    <T> CloseableIterator<FieldValueLookup<T>> select(SqlCommand command) throws Exception;
    <K> CloseableIterator<K> insert(SqlCommand command) throws Exception;
    void execute(SqlCommand command) throws Exception;
}
