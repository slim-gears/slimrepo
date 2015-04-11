// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.FieldValueMap;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.UpdateQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public abstract class AbstractUpdateQuery<TKey, TEntity extends Entity<TKey>> extends AbstractQueryBase<TKey, TEntity, UpdateQuery<TEntity>> implements UpdateQuery<TEntity> {
    protected final List<UpdateFieldInfo> updateFields = new ArrayList<>();

    protected AbstractUpdateQuery(EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> elementType) {
        super(cache, elementType);
    }

    @Override
    protected UpdateQuery<TEntity> self() {
        return this;
    }

    @Override
    public <T> UpdateQuery<TEntity> set(Field<TEntity, T> field, T value) {
        updateFields.add(new UpdateFieldInfo(field, value));
        return this;
    }

    @Override
    public UpdateQuery<TEntity> setAll(TEntity entity) {
        elementType.entityToMap(entity, new FieldValueMap<TEntity>() {
            @Override
            public <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value) {
                if (field != elementType.getKeyField()) updateFields.add(new UpdateFieldInfo(field, value));
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
    public <T> UpdateQuery<TEntity> exclude(final Field<TEntity, T> field) {
        updateFields.removeIf(new java.util.function.Predicate<UpdateFieldInfo>() {
            @Override
            public boolean test(UpdateFieldInfo updateFieldInfo) {
                return updateFieldInfo.field == field;
            }
        });
        return this;
    }
}
