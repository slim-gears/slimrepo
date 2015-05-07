// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface SchemaBuilder {
    <TKey, TEntity extends Entity<TKey>> void createEntityType(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>> void deleteEntityType(EntityType<TKey, TEntity> entityType);
}
