// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.queries;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface EntityDeleteQuery {
    interface Builder<T> extends QueryBuilder<T, EntityDeleteQuery, Builder<T>> {
    }

    void execute() throws IOException;
}
