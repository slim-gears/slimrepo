// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.slimgears.slimrepo.core.interfaces.queries.EntityDeleteQuery;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class DefaultEntityDeleteQuery<TKey, TEntity extends Entity<TKey>>
        extends AbstractEntityQuery<TKey, TEntity, EntityDeleteQuery, EntityDeleteQuery.Builder<TEntity>, DeleteQueryParams<TKey, TEntity>>
        implements EntityDeleteQuery, EntityDeleteQuery.Builder<TEntity> {
    public DefaultEntityDeleteQuery(EntityType<TKey, TEntity> entityType, QueryProvider<TKey, TEntity> queryProvider) {
        super(new DeleteQueryParams<>(entityType, null, null), queryProvider);
    }

    private DefaultEntityDeleteQuery(DeleteQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        super(queryParams, queryProvider);
    }

    private PreparedQuery<Void> preparedDeleteQuery;

    @Override
    protected Builder<TEntity> fork(DeleteQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new DefaultEntityDeleteQuery<>(queryParams, queryProvider);
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
    public EntityDeleteQuery prepare() {
        preparedDeleteQuery = queryProvider.prepareDelete(queryParams);
        return this;
    }
}
