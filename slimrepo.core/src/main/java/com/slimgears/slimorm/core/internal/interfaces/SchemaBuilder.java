// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.interfaces;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface SchemaBuilder {
    <TKey, TEntity extends Entity<TKey>> void createEntityType(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>> void deleteEntityType(EntityType<TKey, TEntity> entityType);
}
