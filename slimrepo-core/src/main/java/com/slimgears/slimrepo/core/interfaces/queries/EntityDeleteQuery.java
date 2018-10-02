// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.queries;

/**
 * Created by Denis on 07-Apr-15
 *
 */
public interface EntityDeleteQuery {
    interface Builder<T> extends QueryBuilder<T, EntityDeleteQuery, Builder<T>> {
    }

    void execute() throws Exception;
}
