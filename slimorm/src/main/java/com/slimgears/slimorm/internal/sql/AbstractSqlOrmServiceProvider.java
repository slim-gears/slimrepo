// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlOrmServiceProvider implements SqlOrmServiceProvider {
    private SqlStatementBuilder sqlBuilder;

    @Override
    public SqlStatementBuilder getStatementBuilder() {
        return sqlBuilder != null
                ? sqlBuilder
                : (sqlBuilder = createStatementBuilder());
    }

    protected SqlStatementBuilder createStatementBuilder() {
        SqlStatementBuilder.SyntaxProvider syntaxProvider = createSyntaxProvider();
        SqlStatementBuilder.PredicateBuilder predicateBuilder = createPredicateBuilder(syntaxProvider);
        return createStatementBuilder(syntaxProvider, predicateBuilder);
    }

    protected SqlStatementBuilder createStatementBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider, SqlStatementBuilder.PredicateBuilder predicateBuilder) {
        return new DefaultSqlStatementBuilder(predicateBuilder, syntaxProvider);
    }

    protected SqlStatementBuilder.PredicateBuilder createPredicateBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider) {
        return new SqlPredicateBuilder(syntaxProvider);
    }

    protected abstract SqlStatementBuilder.SyntaxProvider createSyntaxProvider();
}
