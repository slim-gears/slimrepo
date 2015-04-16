// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.query;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.interfaces.queries.UpdateQuery;
import com.slimgears.slimorm.core.internal.UpdateFieldInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public class EntityUpdateQuery<TKey, TEntity extends Entity<TKey>>
        extends AbstractEntityQuery<TKey, TEntity, UpdateQuery, UpdateQuery.Builder<TEntity>, UpdateQueryParams<TKey, TEntity>>
        implements UpdateQuery, UpdateQuery.Builder<TEntity> {

    public EntityUpdateQuery(EntityType<TKey, TEntity> entityType, QueryProvider<TKey, TEntity> queryProvider) {
        super(new UpdateQueryParams<>(entityType, null, null, null), queryProvider);
    }

    private EntityUpdateQuery(UpdateQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
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
        queryParams.updates.removeIf(new java.util.function.Predicate<UpdateFieldInfo>() {
            @Override
            public boolean test(UpdateFieldInfo updateFieldInfo) {
                return updateFieldInfo.field == field;
            }
        });
        return this;
    }

    private <T> void addUpdate(Field<TEntity, T> field, T value) {
        if (queryParams.updates == null) queryParams.updates = new ArrayList<>();
        queryParams.updates.add(new UpdateFieldInfo(field, value));
    }

    @Override
    protected Builder<TEntity> fork(UpdateQueryParams<TKey, TEntity> queryParams, QueryProvider<TKey, TEntity> queryProvider) {
        return new EntityUpdateQuery<>(queryParams, queryProvider);
    }

    @Override
    protected Builder<TEntity> builder() {
        return this;
    }

    @Override
    public UpdateQuery prepare() {
        preparedUpdateQuery = queryProvider.prepareUpdate(queryParams);
        return this;
    }

    @Override
    public void execute() throws IOException {
        preparedUpdateQuery.execute();
    }
}
