// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.AbstractOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

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

    protected abstract SqlStatementBuilder createStatementBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider, SqlStatementBuilder.PredicateBuilder predicateBuilder);

    protected SqlStatementBuilder.PredicateBuilder createPredicateBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider) {
        return new SqlPredicateBuilder(syntaxProvider);
    }

    @Override
    protected void onMapFieldTypes(FieldTypeMappingRegistrar registrar) {
        SqlRelationalTypeMapper typeMapper = new SqlRelationalTypeMapper();
        registrar.registerConverter(typeMapper, typeMapper);
        super.onMapFieldTypes(registrar);
    }

    protected abstract SqlStatementBuilder.SyntaxProvider createSyntaxProvider();
}
