// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.conditions;

import com.slimgears.slimorm.core.interfaces.fields.Field;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface FieldCondition<TEntity, T> extends Condition<TEntity> {
    Field<TEntity, T> getField();
}
