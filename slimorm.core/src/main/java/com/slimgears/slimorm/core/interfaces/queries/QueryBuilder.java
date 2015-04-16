// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces.queries;

import com.slimgears.slimorm.core.interfaces.predicates.Predicate;

/**
 * Created by Denis on 13-Apr-15
 * <File Description>
 */
public interface QueryBuilder<T, TQuery, TBuilder extends QueryBuilder<T, TQuery, TBuilder>> {
    TBuilder where(Predicate<T> predicate);
    TBuilder limit(int number);
    TBuilder skip(int number);
    TBuilder fork();
    TQuery prepare();
}
