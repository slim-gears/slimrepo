// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class Iterators {
    public static <T> T[] toArray(Iterator<T> iterator, Class<? extends T> itemClass) {
        Collection<T> items = Stream.of(iterator).collect(Collectors.toList());
        //noinspection unchecked
        return items.toArray((T[])Array.newInstance(itemClass, items.size()));
    }

    @SafeVarargs
    public static <T> Iterator<T> forArray(T... array) {
        return Arrays.asList(array).iterator();
    }
}
