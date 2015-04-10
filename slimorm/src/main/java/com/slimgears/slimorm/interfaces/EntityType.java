// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface EntityType<TKey, TEntity extends Entity<TKey>> {
    Class<TEntity> getEntityClass();
    String getName();
    Field<TEntity, ?>[] getFields();
    Field<TEntity, TKey> getKeyField();
    TEntity newInstance();
    TEntity newInstance(FieldValueLookup<TEntity> lookup);
    void entityToMap(TEntity entity, FieldValueMap<TEntity> map);
}
