// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.fields;

/**
 * Created by ditskovi on 12/22/2015.
 */
public interface ValueSetter<TEntity, T> {
    void setValue(TEntity entity, T value);
}
