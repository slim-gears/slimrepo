// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.IOException;

/**
 * Created by Denis on 08-Apr-15
 *
 */
public interface SqlCommandExecutor {
    long count(String statement, String... parameters) throws IOException;
    <T> CloseableIterator<FieldValueLookup<T>> select(String statement, String... parameters) throws IOException;
    void execute(String statement, String... parameters) throws IOException;
}
