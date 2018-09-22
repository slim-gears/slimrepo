// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.interfaces.CloseableIterator;

import java.util.Collection;

/**
 * Created by Denis on 12-Apr-15
 *
 */
public interface QueryProvider<TKey, TEntity> {
    PreparedQuery<CloseableIterator<FieldValueLookup<TEntity>>> prepareSelect(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Long> prepareCount(SelectQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareUpdate(UpdateQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareDelete(DeleteQueryParams<TKey, TEntity> query);
    PreparedQuery<Void> prepareInsert(Collection<TEntity> entities);
}
