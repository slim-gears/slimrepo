// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.TransactionProvider;

import java.io.IOException;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class AbstractRepository implements Repository {
    private final TransactionProvider transactionProvider;
    private final SessionServiceProvider sessionServiceProvider;

    protected AbstractRepository(SessionServiceProvider sessionServiceProvider) {
        this.sessionServiceProvider = sessionServiceProvider;
        this.transactionProvider = sessionServiceProvider.getTransactionProvider();
    }

    @Override
    public <TKey, TEntity> EntitySet<TEntity> entities(EntityType<TKey, TEntity> entityType) {
        return sessionServiceProvider.getEntitySetProvider(entityType).get();
    }

    @Override
    public void saveChanges() throws IOException {
        transactionProvider.beginTransaction();
        try {
            sessionServiceProvider.onSavingChanges(this);
        } catch (Throwable e) {
            transactionProvider.cancelTransaction();
            throw e;
        }
        transactionProvider.commitTransaction();
    }

    @Override
    public void discardChanges() {
        sessionServiceProvider.onDiscardingChanges(this);
    }

    @Override
    public void close() throws IOException {
        sessionServiceProvider.onClosing(this);
        sessionServiceProvider.close();
    }
}
