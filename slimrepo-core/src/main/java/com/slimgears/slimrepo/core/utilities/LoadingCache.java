// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import java.util.concurrent.ExecutionException;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public interface LoadingCache<TKey, TValue> {
    interface Loader<TKey, TValue> {
        TValue load(TKey key) throws Exception;
    }

    TValue get(TKey key) throws ExecutionException;
}
