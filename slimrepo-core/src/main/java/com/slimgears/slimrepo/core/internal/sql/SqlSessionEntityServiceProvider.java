// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.AbstractSessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimrepo.core.internal.query.QueryProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSessionServiceProvider;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public class SqlSessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>>
        extends AbstractSessionEntityServiceProvider<TKey, TEntity>
        implements SessionEntityServiceProvider<TKey, TEntity> {
    protected final SqlSessionServiceProvider serviceProvider;
    protected final EntityType<TKey, TEntity> entityType;
    private QueryProvider<TKey, TEntity> queryProvider;

    public SqlSessionEntityServiceProvider(SqlSessionServiceProvider serviceProvider, EntityType<TKey, TEntity> entityType) {
        this.serviceProvider = serviceProvider;
        this.entityType = entityType;
    }

    @Override
    public QueryProvider<TKey, TEntity> getQueryProvider() {
        return queryProvider != null
                ? queryProvider
                : (queryProvider = createQueryProvider());
    }

    protected QueryProvider<TKey, TEntity> createQueryProvider() {
        return new SqlQueryProvider<>(serviceProvider, entityType);
    }
}
