// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.query;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.queries.EntityUpdateQuery;
import com.slimgears.slimrepo.core.internal.UpdateFieldInfo;

import java.util.ArrayList;

/**
 * Created by Denis on 07-Apr-15
 *
 */
public class DefaultEntityUpdateQuery<TKey, TEntity>
        extends AbstractEntityQuery<TKey, TEntity, EntityUpdateQuery, EntityUpdateQuery.Builder<TEntity>, UpdateQueryParams<TKey, TEntity>>
        implements EntityUpdateQuery, EntityUpdateQuery.Builder<TEntity> {

    public DefaultEntityUpdateQuery(EntityType<TKey, TEntity> entityType, QueryProvider<TKey, TEntity> queryProvider) {
        super(new UpdateQueryParams<>(entityType, null, null, null), queryProvider);
    }

    private DefaultEntityUpdateQuery(UpdateQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        super(queryParams, queryProvider);
    }

    private PreparedQuery<Void> preparedUpdateQuery;

    @Override
    public <T> Builder<TEntity> set(Field<TEntity, T> field, T value) {
        addUpdate(field, value);
        return this;
    }

    @Override
    public Builder<TEntity> setAll(TEntity entity) {
        queryParams.entityType.entityToMap(entity, new FieldValueMap<TEntity>() {
            @Override
            public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
                if (field != queryParams.entityType.getKeyField()) addUpdate(field, value);
                return this;
            }

            @Override
            public <T> T getValue(Field<TEntity, T> field) {
                return null;
            }
        });
        return this;
    }

    @Override
    public <T> Builder<TEntity> exclude(final Field<TEntity, T> field) {
        queryParams.updates = Stream.of(queryParams.updates).filter(i -> i.field != field).collect(Collectors.toList());
        return this;
    }

    private <T> void addUpdate(Field<TEntity, T> field, T value) {
        if (queryParams.updates == null) queryParams.updates = new ArrayList<>();
        queryParams.updates.add(new UpdateFieldInfo(field, value));
    }

    @Override
    protected Builder<TEntity> fork(UpdateQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new DefaultEntityUpdateQuery<>(queryParams, queryProvider);
    }

    @Override
    protected Builder<TEntity> builder() {
        return this;
    }

    @Override
    public EntityUpdateQuery prepare() {
        preparedUpdateQuery = queryProvider.prepareUpdate(queryParams);
        return this;
    }

    @Override
    public void execute() throws Exception {
        preparedUpdateQuery.execute();
    }
}
