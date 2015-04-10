// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Field;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlEntityType<TKey, TEntity extends Entity<TKey>> implements EntityType<TKey, TEntity> {
    private final String name;
    private final Class<TEntity> entityClass;
    private final Field<TEntity, TKey> keyField;
    private final Field[] fields;

    protected AbstractSqlEntityType(String name, Class<TEntity> entityClass, Field<TEntity, TKey> keyField, Field... fields) {
        this.name = name;
        this.entityClass = entityClass;
        this.keyField = keyField;
        this.fields = fields;
    }

    @Override
    public Class<TEntity> getEntityClass() {
        return entityClass;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Field[] getFields() {
        return fields;
    }

    @Override
    public Field<TEntity, TKey> getKeyField() {
        return keyField;
    }
}
