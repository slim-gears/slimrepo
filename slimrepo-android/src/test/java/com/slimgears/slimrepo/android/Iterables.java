// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.android;

import android.annotation.SuppressLint;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Objects;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class Iterables {
    @SuppressLint("NewApi")
    public static <T> boolean elementsEqual(Iterable<T> first, Iterable<T> second) {
        List<T> firstCollection = Stream.of(first).collect(Collectors.toList());
        List<T> secondCollection = Stream.of(second).collect(Collectors.toList());
        if (firstCollection.size() != secondCollection.size()) return false;

        return Stream
                .ofRange(0, firstCollection.size())
                .allMatch(i -> Objects.equals(firstCollection.get(i), secondCollection.get(i)));
    }
}
