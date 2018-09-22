// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

/**
 * Created by Denis on 15-Apr-15
 *
 */
public abstract class AbstractSqliteOrmServiceProvider extends AbstractSqlOrmServiceProvider {
    @Override
    protected SqlStatementBuilder.SyntaxProvider createSyntaxProvider() {
        return new SqliteSyntaxProvider(getFieldTypeMapperRegistrar());
    }

    @Override
    protected SqlStatementBuilder createStatementBuilder(SqlStatementBuilder.SyntaxProvider syntaxProvider, SqlStatementBuilder.PredicateBuilder predicateBuilder) {
        return new SqliteStatementBuilder(predicateBuilder, syntaxProvider);
    }

    @Override
    protected void onMapFieldTypes(FieldTypeMappingRegistrar registrar) {
        SqliteTypeMappers.INSTANCE.install(registrar);
        super.onMapFieldTypes(registrar);
    }
}
