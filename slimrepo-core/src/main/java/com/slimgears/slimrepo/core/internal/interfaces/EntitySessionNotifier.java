package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by Denis on 03-May-15.
 */
public interface EntitySessionNotifier {
    void addListener(EntityType<?, ?> entityType, RepositorySessionNotifier.Listener listener);
    void removeListener(EntityType<?, ?> entityType);
}
