// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.utilities;


import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Arrays;

/**
 * Created by ditskovi on 2/20/2016.
 *
 */
public class Joiner {
    private final String separator;

    private Joiner(String separator) {
        this.separator = separator;
    }
    
    public static Joiner on(String separator) {
        return new Joiner(separator);
    }
    
    public static Joiner on(char seperator) {
        return on(String.valueOf(seperator));
    }

    @SafeVarargs
    public final <T> String join(T... values) {
        return join(Arrays.asList(values));
    }

    public <T> String join(Iterable<T> values) {
        return Stream.of(values)
                .map(Object::toString)
                .collect(Collectors.joining(separator));
    }
}
