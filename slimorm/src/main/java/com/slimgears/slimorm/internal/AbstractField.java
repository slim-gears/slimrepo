// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.Field;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public abstract class AbstractField<TEntity, T> implements Field<TEntity, T> {
    private final String name;
    private final Class<T> type;

    protected AbstractField(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }
}
