// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class HashMapLoadingCache<TKey, TValue> implements LoadingCache<TKey, TValue> {
    private final Map<TKey, TValue> cache = new HashMap<>();
    private final Object lock = new Object();
    private final Loader<TKey, TValue> loader;

    public HashMapLoadingCache(Loader<TKey, TValue> loader) {
        this.loader = loader;
    }

    @Override
    public TValue get(TKey key) throws ExecutionException {
        if (!cache.containsKey(key)) {
            synchronized (lock) {
                if (!cache.containsKey(key)) {
                    TValue value = null;
                    try {
                        value = loader.load(key);
                    } catch (Exception e) {
                        throw new ExecutionException(e);
                    }
                    cache.put(key, value);
                    return value;
                }
            }
        }
        return cache.get(key);
    }

    public static <TKey, TValue> LoadingCache<TKey, TValue> newCache(Loader<TKey, TValue> loader) {
        return new HashMapLoadingCache<>(loader);
    }
}
