// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.google.common.collect.Iterables;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Predicate;
import com.slimgears.slimorm.interfaces.Field;
import com.slimgears.slimorm.interfaces.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Denis on 07-Apr-15
 * <File Description>
 */
public abstract class AbstractQuery<TKey, TEntity extends Entity<TKey>> extends AbstractQueryBase<TKey, TEntity, Query<TEntity>> implements Query<TEntity> {

    protected final List<OrderFieldInfo> orderFields = new ArrayList<>();

    private void addOrderFields(boolean ascending, Field... fields) {
        for (Field field : fields) {
            orderFields.add(new OrderFieldInfo(field, ascending));
        }
    }

    @Override
    protected Query<TEntity> self() {
        return this;
    }

    protected AbstractQuery(EntityCache<TKey, TEntity> cache, EntityType<TKey, TEntity> elementType) {
        super(cache, elementType);
    }

    protected abstract Iterator<TEntity> execute() throws IOException;
    protected abstract int executeCount() throws IOException;

    @Override
    public Iterator<TEntity> iterator() {
        try {
            return execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Query<TEntity> orderAsc(Field<TEntity, ?>... fields) {
        addOrderFields(true, fields);
        return this;
    }

    @Override
    public Query<TEntity> orderDesc(Field<TEntity, ?>... fields) {
        addOrderFields(false, fields);
        return this;
    }

    @Override
    public TEntity firstOrDefault() throws IOException {
        try {
            return Iterables.getFirst(this, null);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public TEntity singleOrDefault() throws IOException {
        return firstOrDefault();
    }

    @Override
    public List<TEntity> toList() throws IOException {
        return Arrays.asList(toArray());
    }

    @Override
    public TEntity[] toArray() throws IOException {
        try {
            return Iterables.toArray(this, elementType.getEntityClass());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) throw (IOException)e.getCause();
            throw e;
        }
    }

    @Override
    public int count() throws IOException {
        return executeCount();
    }
}
