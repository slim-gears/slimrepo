// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.entities;

import com.slimgears.slimorm.interfaces.fields.Field;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public interface FieldValueLookup<TEntity> {
    <T> T getValue(Field<TEntity, T> field);
}
