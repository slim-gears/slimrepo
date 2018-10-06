// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.annimon.stream.function.Function;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;

import java.util.ArrayList;
import java.util.List;

/**
* Created by Denis on 08-Apr-15
*
*/
public class SqlCommands {
    public static SqlCommand.Builder builder() {
        return new Builder();
    }

    static class Builder implements SqlCommand.Builder {
        private final StringBuilder builder = new StringBuilder();
        private final List<SqlCommand.Parameter<?>> params = new ArrayList<>();

        public Builder append(String statement) {
            builder.append(statement);
            return this;
        }

        @Override
        public SqlCommand.Builder append(Function<SqlCommand.Builder, String> statement) {
            return append(statement.apply(this));
        }

        @Override
        public <T> int addParam(Class<T> type, T value) {
            int index = params.size();
            params.add(new SqlCommand.Parameter<T>() {
                @Override
                public Class<T> type() {
                    return type;
                }

                @Override
                public T value() {
                    return value;
                }
            });
            return index;
        }

        @Override
        public <T> int addParam(T value) {
            return addParam(paramType(value), value);
        }

        public <T> Builder param(Class<T> type, T value) {
            addParam(type, value);
            return this;
        }

        @Override
        public <T> SqlCommand.Builder param(T value) {
            return this.param(paramType(value), value);
        }

        public SqlCommand build() {
            return new SqlCommand() {
                @Override
                public String getStatement() {
                    return builder.toString();
                }

                @Override
                public List<Parameter<?>> getParameters() {
                    return params;
                }
            };
        }

        private <T> Class<T> paramType(Object param) {
            //noinspection unchecked
            return param != null ? (Class<T>)param.getClass() : (Class<T>)String.class;
        }
    }
}
