// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface StringField<TEntity> extends Field<TEntity, String> {
    Predicate<TEntity> contains(String substr);
    Predicate<TEntity> startsWith(String substr);
    Predicate<TEntity> endsWith(String substr);
}
