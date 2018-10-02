package com.slimgears.slimrepo.core.internal;

import com.annimon.stream.Stream;
import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.interfaces.EntitySessionNotifier;
import com.slimgears.slimrepo.core.internal.interfaces.RepositorySessionNotifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 03-May-15.
 *
 */
public class DefaultEntitySessionNotifier implements EntitySessionNotifier, RepositorySessionNotifier.Listener {
    private final Map<EntityType, RepositorySessionNotifier.Listener> listenerMap = new HashMap<>();

    public DefaultEntitySessionNotifier(RepositorySessionNotifier notifier) {
        notifier.addListener(this);
    }

    interface Notifier {
        void notify(RepositorySessionNotifier.Listener listener) throws Exception;
    }

    @Override
    public void addListener(EntityType<?, ?> entityType, RepositorySessionNotifier.Listener listener) {
        listenerMap.put(entityType, listener);
    }

    @Override
    public void removeListener(EntityType<?, ?> entityType) {
        listenerMap.remove(entityType);
    }

    @Override
    public void onSavingChanges(final Repository session) throws Exception {
        notify(listener -> listener.onSavingChanges(session));
    }

    @Override
    public void onDiscardingChanges(final Repository session) {
        try {
            notify(listener -> listener.onDiscardingChanges(session));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClosing(final Repository session) {
        try {
            notify(listener -> listener.onClosing(session));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void notify(Notifier notifier) throws Exception {
        notify(new HashMap<>(listenerMap), notifier);
    }

    private void notify(Map<EntityType, RepositorySessionNotifier.Listener> listeners, Notifier notifier) throws Exception {
        while (!listeners.isEmpty()) {
            EntityType<?, ?> entityType = Stream.of(listeners.keySet()).findFirst().get();
            notifyEntityType(entityType, listeners, notifier);
        }
    }

    private void notifyEntityType(EntityType<?, ?> entityType, Map<EntityType, RepositorySessionNotifier.Listener> listeners, Notifier notifier) throws Exception {
        RepositorySessionNotifier.Listener listener = listeners.remove(entityType);
        for (RelationalField<?, ?> field : entityType.getRelationalFields()) {
            EntityType<?, ?> relatedType = field.metaInfo().getRelatedEntityType();
            if (listeners.containsKey(relatedType)) {
                notifyEntityType(relatedType, listeners, notifier);
            }
        }

        notifier.notify(listener);
    }
}
