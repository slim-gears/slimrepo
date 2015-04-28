// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.conditions;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface FieldCondition<TEntity extends Entity<?>, T> extends Condition<TEntity> {
    Field<TEntity, T> getField();
}
