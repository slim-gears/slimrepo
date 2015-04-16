// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.internal.interfaces.SessionServiceProvider;
import com.slimgears.slimorm.internal.interfaces.TransactionProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public class AbstractRepositorySession implements RepositorySession {
    private final List<OnSaveChangesListener> onSaveChangesListeners = new ArrayList<>();
    private final List<OnDiscardChangesListener> onDiscardChangesListeners = new ArrayList<>();
    private final TransactionProvider transactionProvider;
    private final SessionServiceProvider sessionServiceProvider;

    protected AbstractRepositorySession(SessionServiceProvider sessionServiceProvider) {
        this.sessionServiceProvider = sessionServiceProvider;
        this.transactionProvider = sessionServiceProvider.getTransactionProvider();
    }

    @Override
    public void saveChanges() throws IOException {
        transactionProvider.beginTransaction();
        try {
            for (OnSaveChangesListener listener : onSaveChangesListeners) {
                listener.onSavingChanges(this);
            }
        } catch (Throwable e) {
            transactionProvider.cancelTransaction();
            throw e;
        }
        transactionProvider.commitTransaction();
    }

    @Override
    public void discardChanges() {
        for (OnDiscardChangesListener listener : onDiscardChangesListeners) {
            listener.onDiscardingChanges(this);
        }
    }

    @Override
    public void addOnSaveChangesListener(OnSaveChangesListener listener) {
        onSaveChangesListeners.add(listener);
    }

    @Override
    public void removeOnSaveChangesListener(OnSaveChangesListener listener) {
        onSaveChangesListeners.remove(listener);
    }

    @Override
    public void addOnDiscardChangesListener(OnDiscardChangesListener listener) {
        onDiscardChangesListeners.add(listener);
    }

    @Override
    public void removeOnDiscardChangesListener(OnDiscardChangesListener listener) {
        onDiscardChangesListeners.remove(listener);
    }

    @Override
    public void close() throws IOException {
        sessionServiceProvider.close();
    }
}
