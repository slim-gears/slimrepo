// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Map;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class Maps {
    public interface KeyGetter<TKey, TValue> {
        TKey getKey(TValue value);
    }

    public interface ValueGetter<TKey, TValue> {
        TValue getValue(TKey key);
    }

    public static <TKey, TValue> Map<TKey, TValue> uniqueIndex(Iterable<TValue> values, KeyGetter<TKey, TValue> keyGetter) {
        return Stream.of(values).collect(Collectors.toMap(keyGetter::getKey, i -> i));
    }

    public static <TKey, TValue> Map<TKey, TValue> asMap(Iterable<TKey> keys, ValueGetter<TKey, TValue> valueGetter) {
        return Stream.of(keys).collect(Collectors.toMap(k -> k, valueGetter::getValue));
    }
}
