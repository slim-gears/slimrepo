// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.Predicate;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface StringField<TEntity> extends ValueField<TEntity, String> {
    Predicate<TEntity> contains(String substr);
    Predicate<TEntity> notContains(String substr);
    Predicate<TEntity> startsWith(String substr);
    Predicate<TEntity> endsWith(String substr);
    Predicate<TEntity> notStartsWith(String substr);
    Predicate<TEntity> notEndsWith(String substr);
}
