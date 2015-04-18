// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.interfaces;

import com.slimgears.slimorm.core.interfaces.RepositorySession;

import java.io.IOException;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public interface RepositorySessionNotifier {
    interface Listener {
        void onSavingChanges(RepositorySession session) throws IOException;
        void onDiscardingChanges(RepositorySession session);
        void onClosing(RepositorySession session);
    }

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
