// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.entities;

import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.fields.ValueField;

import java.util.Collection;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface EntityType<TKey, TEntity extends Entity<TKey>> {
    Class<TEntity> getEntityClass();
    String getName();
    ValueField<TEntity, TKey> getKeyField();
    Collection<Field<TEntity, ?>> getFields();
    Collection<EntityType> getRelatedEntities();
    TEntity newInstance();
    TEntity newInstance(FieldValueLookup<TEntity> lookup);
    void entityToMap(TEntity entity, FieldValueMap<TEntity> map);
}
