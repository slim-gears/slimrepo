// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.internal.sql.AbstractSqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlStatementBuilder;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public abstract class AbstractSqliteOrmServiceProvider extends AbstractSqlOrmServiceProvider {
    @Override
    protected SqlStatementBuilder.SyntaxProvider createSyntaxProvider() {
        return new SqliteSyntaxProvider();
    }
}
