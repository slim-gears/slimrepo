// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.Repository;

import java.io.IOException;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public interface RepositorySessionNotifier {
    interface Listener {
        void onSavingChanges(Repository session) throws IOException;
        void onDiscardingChanges(Repository session);
        void onClosing(Repository session);
    }

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
