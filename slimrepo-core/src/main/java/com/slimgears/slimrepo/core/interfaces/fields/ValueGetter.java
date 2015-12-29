// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

/**
 * Created by ditskovi on 12/22/2015.
 */
public interface ValueGetter<TEntity, T> {
    T getValue(TEntity entity);
}
