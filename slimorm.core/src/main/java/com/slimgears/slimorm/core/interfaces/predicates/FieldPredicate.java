// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.predicates;

import com.slimgears.slimorm.core.interfaces.fields.Field;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public interface FieldPredicate<TEntity, T> extends Predicate<TEntity> {
    Field<TEntity, T> getField();
}
