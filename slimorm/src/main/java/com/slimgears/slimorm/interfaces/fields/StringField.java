// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces.fields;

import com.slimgears.slimorm.interfaces.predicates.BinaryPredicate;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public interface StringField<TEntity> extends ValueField<TEntity, String> {
    BinaryPredicate<TEntity, String> contains(String substr);
    BinaryPredicate<TEntity, String> notContains(String substr);
    BinaryPredicate<TEntity, String> startsWith(String substr);
    BinaryPredicate<TEntity, String> endsWith(String substr);
    BinaryPredicate<TEntity, String> notStartsWith(String substr);
    BinaryPredicate<TEntity, String> notEndsWith(String substr);
}
