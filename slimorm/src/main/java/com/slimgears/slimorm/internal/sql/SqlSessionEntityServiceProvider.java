// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.internal.AbstractSessionEntityServiceProvider;
import com.slimgears.slimorm.internal.interfaces.SessionEntityServiceProvider;
import com.slimgears.slimorm.internal.query.QueryProvider;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public class SqlSessionEntityServiceProvider<TKey, TEntity extends Entity<TKey>>
        extends AbstractSessionEntityServiceProvider<TKey, TEntity>
        implements SessionEntityServiceProvider<TKey, TEntity> {
    private final SqlSessionServiceProvider serviceProvider;
    private final EntityType<TKey, TEntity> entityType;
    private QueryProvider<TKey, TEntity> queryProvider;

    public SqlSessionEntityServiceProvider(SqlSessionServiceProvider serviceProvider, EntityType<TKey, TEntity> entityType) {
        this.serviceProvider = serviceProvider;
        this.entityType = entityType;
    }

    @Override
    public QueryProvider<TKey, TEntity> getQueryProvider() {
        return queryProvider != null
                ? queryProvider
                : (queryProvider = new SqlQueryProvider<>(serviceProvider, entityType));
    }
}
