// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.io.Closeable;

/**
* Created by Denis on 09-Apr-15
*
*/
public interface Repository extends Closeable {
    <TKey, TEntity> EntitySet<TEntity> entities(EntityType<TKey, TEntity> entityType);
    void saveChanges() throws Exception;
    void discardChanges();
}
