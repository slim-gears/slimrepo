// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.io.IOException;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public interface SqlCommandExecutor {
    long count(SqlCommand command) throws IOException;
    <T> CloseableIterator<FieldValueLookup<T>> select(SqlCommand command) throws IOException;
    void execute(SqlCommand command) throws IOException;
}
