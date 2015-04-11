// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.CloseableIterator;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public interface SqlCommandExecutor extends Closeable {
    int count(SqlCommand command) throws IOException;
    <T> CloseableIterator<FieldValueLookup<T>> select(SqlCommand command) throws IOException;
    void execute(SqlCommand command) throws IOException;
    void beginTransaction() throws IOException;
    void cancelTransaction() throws IOException;
    void commitTransaction() throws IOException;
}
