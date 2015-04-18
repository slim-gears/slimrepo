// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.queries;

import com.slimgears.slimorm.core.interfaces.conditions.Condition;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public interface QueryBuilder<T, TQuery, TBuilder extends QueryBuilder<T, TQuery, TBuilder>> {
    TBuilder where(Condition<T> condition);
    TBuilder limit(int number);
    TBuilder skip(int number);
    TBuilder fork();
    TQuery prepare();
}
