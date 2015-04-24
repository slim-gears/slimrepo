// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.AbstractOrmServiceProvider;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlOrmServiceProvider extends AbstractOrmServiceProvider implements SqlOrmServiceProvider {
    private SqlStatementBuilder sqlBuilder;
    private SqlStatementBuilder.SyntaxProvider syntaxProvider;

    @Override
    public SqlStatementBuilder getStatementBuilder() {
        return sqlBuilder != null
                ? sqlBuilder
                : (sqlBuilder = createStatementBuilder());
    }

    @Override
    public SqlStatementBuilder.SyntaxProvider getSyntaxProvider() {
        return syntaxProvider != null
                ? syntaxProvider
                : (syntaxProvider = createSyntaxProvider());
    }

    protected SqlStatementBuilder createStatementBuilder() {
        SqlStatementBuilder.SyntaxProvider syntaxProvider = getSyntaxProvider();
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
