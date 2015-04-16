// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.query;

import com.slimgears.slimorm.interfaces.queries.DeleteQuery;
import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class EntityDeleteQuery<TKey, TEntity extends Entity<TKey>>
        extends AbstractEntityQuery<TKey, TEntity, DeleteQuery, DeleteQuery.Builder<TEntity>, DeleteQueryParams<TKey, TEntity>>
        implements DeleteQuery, DeleteQuery.Builder<TEntity> {
    public EntityDeleteQuery(EntityType<TKey, TEntity> entityType, QueryProvider<TKey, TEntity> queryProvider) {
        super(new DeleteQueryParams<>(entityType, null, null), queryProvider);
    }

    private EntityDeleteQuery(DeleteQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        super(queryParams, queryProvider);
    }

    private PreparedQuery<Void> preparedDeleteQuery;

    @Override
    protected Builder<TEntity> fork(DeleteQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new EntityDeleteQuery<>(queryParams, queryProvider);
    }

    @Override
    protected Builder<TEntity> builder() {
        return this;
    }

    @Override
    public void execute() throws IOException {
        preparedDeleteQuery.execute();
    }

    @Override
    public DeleteQuery prepare() {
        preparedDeleteQuery = queryProvider.prepareDelete(queryParams);
        return this;
    }
}
