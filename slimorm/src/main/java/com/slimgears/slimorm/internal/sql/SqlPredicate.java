// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.Predicate;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
class SqlPredicate<TEntity> implements Predicate<TEntity>, SqlStatementBuilder.PredicateBuilder {
    private final String expression;
    private final Object[] arguments;

    public SqlPredicate(String expression, Object... args) {
        this.expression = expression;
        this.arguments = args;
    }

    @Override
    public Predicate<TEntity> and(Predicate other) {
        return new SqlPredicate<>("(%1$s) AND (%1$s)", this, other);
    }

    @Override
    public Predicate<TEntity> or(Predicate other) {
        return new SqlPredicate<>("(%1$s) OR (%1$s)", this, other);
    }

    @Override
    public String build(SqlCommand.Parameters parameters) {
        Object[] args = new String[arguments.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = arguments[i];
            args[i] = (arg instanceof SqlStatementBuilder.PredicateBuilder)
                    ? ((SqlStatementBuilder.PredicateBuilder)arg).build(parameters)
                    : parameters.add(arg);
        }
        return String.format(expression, args);
    }
}
