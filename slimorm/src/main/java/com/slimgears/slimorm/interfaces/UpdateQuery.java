// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.predicates.Predicate;

import java.io.IOException;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public interface UpdateQuery<TEntity> {
    UpdateQuery<TEntity> where(Predicate<TEntity> predicate);
    <T> UpdateQuery<TEntity> set(Field<TEntity, T> field, T value);
    UpdateQuery<TEntity> setAll(TEntity entity);
    <T> UpdateQuery<TEntity> exclude(Field<TEntity, T> field);
    void execute() throws IOException;
}
