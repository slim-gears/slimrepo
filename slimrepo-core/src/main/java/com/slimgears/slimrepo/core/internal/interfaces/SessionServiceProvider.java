// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

import java.io.Closeable;

/**
 * Created by Denis on 14-Apr-15
 * <File Description>
 */
public interface SessionServiceProvider extends RepositorySessionNotifier, RepositorySessionNotifier.Listener, Closeable {
    <TKey, TEntity> SessionEntityServiceProvider<TKey, TEntity> getEntityServiceProvider(EntityType<TKey, TEntity> entityType);
    <TKey, TEntity>EntitySet.Provider<TEntity> getEntitySetProvider(EntityType<TKey, TEntity> entityType);

    TransactionProvider getTransactionProvider();
    RepositoryCreator getRepositoryCreator();
    OrmServiceProvider getOrmServiceProvider();
    EntitySessionNotifier getEntitySessionNotifier();
}
