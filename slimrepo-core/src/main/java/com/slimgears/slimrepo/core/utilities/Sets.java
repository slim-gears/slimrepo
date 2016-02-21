// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class Sets {
    public static <T> Set<T> difference(Set<T> first, Set<T> second) {
        Set<T> newSet = new HashSet<>(first);
        newSet.removeAll(second);
        return newSet;
    }

    public static <T> Set<T> intersection(Set<T> first, Set<T> second) {
        return Stream.of(first).filter(second::contains).collect(Collectors.toSet());
    }

    public static <T> Set<T> filter(Set<T> set, Predicate<T> predicate) {
        return Stream.of(set).filter(predicate).collect(Collectors.toSet());
    }
}
