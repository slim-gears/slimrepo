// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.RepositorySession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public abstract class AbstractRepositorySession implements RepositorySession {
    private final List<OnSaveChangesListener> onSaveChangesListeners = new ArrayList<>();
    private final List<OnDiscardChangesListener> onDiscardChangesListeners = new ArrayList<>();

    @Override
    public void saveChanges() throws IOException {
        beginTransaction();
        try {
            for (OnSaveChangesListener listener : onSaveChangesListeners) {
                listener.onSavingChanges(this);
            }
        } catch (Throwable e) {
            cancelTransaction();
            throw e;
        }
        commitTransaction();
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

    protected abstract void beginTransaction() throws IOException;
    protected abstract void commitTransaction() throws IOException;
    protected abstract void cancelTransaction() throws IOException;
}
