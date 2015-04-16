// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.interfaces;

import com.slimgears.slimorm.core.interfaces.entities.Entity;
import com.slimgears.slimorm.core.interfaces.entities.EntitySet;
import com.slimgears.slimorm.core.interfaces.entities.EntityType;

import java.io.Closeable;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public interface SessionServiceProvider extends Closeable {
    <TKey, TEntity extends Entity<TKey>> SessionEntityServiceProvider<TKey, TEntity> getEntityServiceProvider(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity extends Entity<TKey>>EntitySet.Provider<TKey, TEntity> getEntitySetProvider(EntityType<TKey, TEntity> entityType);
    TransactionProvider getTransactionProvider();
    RepositoryCreator getRepositoryCreator();
}
