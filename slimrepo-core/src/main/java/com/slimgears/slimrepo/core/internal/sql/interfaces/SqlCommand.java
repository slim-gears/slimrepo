// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.annimon.stream.function.Function;

import java.util.List;

/**
* Created by Denis on 08-Apr-15
*
*/
public interface SqlCommand {
    String getStatement();
    List<Parameter<?>> getParameters();

    interface Parameter<T> {
        Class<T> type();
        T value();
    }

    interface Builder {
        Builder append(String statement);
        Builder append(Function<Builder, String> statement);
        <T> int addParam(Class<T> type, T value);
        <T> int addParam(T value);

        <T> Builder param(Class<T> type, T value);
        <T> Builder param(T value);
        SqlCommand build();
    }
}
