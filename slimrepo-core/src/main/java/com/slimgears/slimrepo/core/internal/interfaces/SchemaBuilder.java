// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public interface SchemaBuilder {
    <TKey, TEntity> void createEntityType(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity> void deleteEntityType(EntityType<TKey, TEntity> entityType);
}
