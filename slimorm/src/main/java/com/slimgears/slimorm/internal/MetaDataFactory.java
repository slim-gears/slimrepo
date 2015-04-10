// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Field;
import com.slimgears.slimorm.interfaces.NumberField;
import com.slimgears.slimorm.interfaces.StringField;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface MetaDataFactory {
    <TEntity, T> Field<TEntity, T> createDataField(Class<TEntity> entityClass, String name, Class<T> type);
    <TEntity, T> NumberField<TEntity, T> createNumberField(Class<TEntity> entityClass, String name, Class<T> numberType);
    <TEntity> StringField<TEntity> createStringField(Class<TEntity> entityClass, String name);
}
