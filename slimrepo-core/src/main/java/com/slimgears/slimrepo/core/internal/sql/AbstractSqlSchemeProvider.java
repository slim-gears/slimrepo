package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlDatabaseScheme;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlSchemeProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

/**
 * Created by Denis on 21-May-15.
 */
public abstract class AbstractSqlSchemeProvider implements SqlSchemeProvider {
    private final SqlStatementBuilder.SyntaxProvider syntaxProvider;

    protected AbstractSqlSchemeProvider(SqlStatementBuilder.SyntaxProvider syntaxProvider) {
        this.syntaxProvider = syntaxProvider;
    }

    @Override
    public SqlDatabaseScheme getModelScheme(RepositoryModel model) {
        return new RepositorySqlDatabaseScheme(syntaxProvider, model);
    }
}
