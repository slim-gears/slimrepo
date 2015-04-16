// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.query;

import com.google.common.collect.Iterators;
import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.queries.Query;
import com.slimgears.slimorm.internal.interfaces.CloseableIterator;
import com.slimgears.slimorm.internal.OrderFieldInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class EntitySelectQuery<TKey, TEntity extends Entity<TKey>>
        extends AbstractEntityQuery<TKey, TEntity, Query<TEntity>, Query.Builder<TEntity>, SelectQueryParams<TKey, TEntity>>
        implements Query<TEntity>, Query.Builder<TEntity> {
    public EntitySelectQuery(EntityType<TKey, TEntity> entityType, QueryProvider<TKey, TEntity> queryProvider) {
        super(new SelectQueryParams<>(entityType, null, null, null), queryProvider);
    }

    private PreparedQuery<CloseableIterator<TEntity>> preparedSelectQuery;
    private PreparedQuery<Integer> preparedCountQuery;

    @Override
    protected Builder<TEntity> fork(SelectQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new EntitySelectQuery<>(queryParams, queryProvider);
    }

    @Override
    protected Builder<TEntity> builder() {
        return this;
    }

    private EntitySelectQuery(SelectQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        super(queryParams, queryProvider);
    }

    @Override
    public CloseableIterator<TEntity> iterator() {
        try {
            return getPreparedSelectQuery().execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Builder<TEntity> orderAsc(Field<TEntity, ?>... fields) {
        addOrderFields(true, fields);
        return builder();
    }

    @Override
    public Builder<TEntity> orderDesc(Field<TEntity, ?>... fields) {
        addOrderFields(false, fields);
        return builder();
    }

    @Override
    public TEntity firstOrDefault() throws IOException {
        try (CloseableIterator<TEntity> iterator = iterator()) {
            return (iterator.hasNext()) ? iterator.next() : null;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public TEntity singleOrDefault() throws IOException {
        return firstOrDefault();
    }

    @Override
    public List<TEntity> toList() throws IOException {
        return Arrays.asList(toArray());
    }

    @Override
    public TEntity[] toArray() throws IOException {
        try (CloseableIterator<TEntity> entities = iterator()) {
            return Iterators.toArray(entities, queryParams.entityType.getEntityClass());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public int count() throws IOException {
        return getPreparedCountQuery().execute();
    }

    private void addOrderFields(boolean ascending, Field... fields) {
        if (queryParams.order == null) queryParams.order = new ArrayList<>();

        for (Field field : fields) {
            queryParams.order.add(new OrderFieldInfo(field, ascending));
        }
    }

    @Override
    public Query<TEntity> prepare() {
        return this;
    }

    private PreparedQuery<CloseableIterator<TEntity>> getPreparedSelectQuery() throws IOException {
        if (preparedSelectQuery != null) return preparedSelectQuery;
        return preparedSelectQuery = queryProvider.prepareSelect(queryParams);
    }

    private PreparedQuery<Integer> getPreparedCountQuery() throws IOException {
        if (preparedCountQuery != null) return preparedCountQuery;
        return preparedCountQuery = queryProvider.prepareCount(queryParams);
    }
}
