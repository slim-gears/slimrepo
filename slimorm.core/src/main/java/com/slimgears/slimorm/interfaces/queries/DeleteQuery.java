// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.queries;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface DeleteQuery {
    interface Builder<T> extends QueryBuilder<T, DeleteQuery, Builder<T>> {
    }

    void execute() throws IOException;
}
