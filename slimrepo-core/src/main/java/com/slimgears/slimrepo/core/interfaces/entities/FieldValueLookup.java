// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.entities;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 09-Apr-15
 *
 */
public interface FieldValueLookup<TEntity> {
    interface Provider<TEntity> {
        FieldValueLookup<TEntity> get();
    }

    <T> T getValue(Field<TEntity, T> field);
}
