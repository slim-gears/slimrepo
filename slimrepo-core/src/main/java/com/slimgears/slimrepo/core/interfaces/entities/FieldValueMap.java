// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public interface FieldValueMap<TEntity> extends FieldValueLookup<TEntity> {
    <T> FieldValueMap<TEntity> putValue(Field<TEntity, T> field, T value);
}
