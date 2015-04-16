// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.interfaces;

import java.io.Closeable;
import java.io.IOException;

/**
* Created by Denis on 09-Apr-15
* <File Description>
*/
public interface RepositorySession extends Closeable {
    interface OnSaveChangesListener {
        void onSavingChanges(RepositorySession session) throws IOException;
    }

    interface OnDiscardChangesListener {
        void onDiscardingChanges(RepositorySession session);
    }

    void saveChanges() throws IOException;
    void discardChanges();

    void addOnSaveChangesListener(OnSaveChangesListener listener);
    void removeOnSaveChangesListener(OnSaveChangesListener listener);

    void addOnDiscardChangesListener(OnDiscardChangesListener listener);
    void removeOnDiscardChangesListener(OnDiscardChangesListener listener);
}
