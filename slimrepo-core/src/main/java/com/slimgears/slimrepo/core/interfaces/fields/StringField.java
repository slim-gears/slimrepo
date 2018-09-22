// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;

/**
 * Created by Denis on 06-Apr-15
 *
 */
public interface StringField<TEntity> extends ValueField<TEntity, String> {
    Condition<TEntity> contains(String substr);
    Condition<TEntity> notContains(String substr);
    Condition<TEntity> startsWith(String substr);
    Condition<TEntity> endsWith(String substr);
    Condition<TEntity> notStartsWith(String substr);
    Condition<TEntity> notEndsWith(String substr);
}
