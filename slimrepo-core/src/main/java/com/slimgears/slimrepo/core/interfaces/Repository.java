// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.io.Closeable;
import java.io.IOException;

/**
* Created by Denis on 09-Apr-15
* <File Description>
*/
public interface Repository extends Closeable {
    <TKey, TEntity extends Entity<TKey>> EntitySet<TEntity> entities(EntityType<TKey, TEntity> entityType);
    void saveChanges() throws IOException;
    void discardChanges();
}
