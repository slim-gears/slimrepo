// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.interfaces;

import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.predicates.Predicate;
import com.slimgears.slimorm.internal.CloseableIterator;

import java.io.IOException;
import java.util.List;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public interface Query<TEntity> {
    Query<TEntity> where(Predicate<TEntity> predicate);
    Query<TEntity> orderAsc(Field<TEntity, ?>... fields);
    Query<TEntity> orderDesc(Field<TEntity, ?>... fields);
    Query<TEntity> limit(int number);
    Query<TEntity> skip(int number);
    TEntity firstOrDefault() throws IOException;
    TEntity singleOrDefault() throws IOException;
    List<TEntity> toList() throws IOException;
    TEntity[] toArray() throws IOException;
    int count() throws IOException;
    CloseableIterator<TEntity> iterator();
}
