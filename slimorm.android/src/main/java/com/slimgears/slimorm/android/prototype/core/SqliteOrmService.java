// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.core;

import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.internal.sql.AbstractSqlOrmService;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlOrmService;
import com.slimgears.slimorm.internal.sql.SqlPredicateBuilder;
import com.slimgears.slimorm.internal.sql.SqlStatementBuilder;
import com.slimgears.slimorm.internal.sql.SqlStatementBuilderImpl;
import com.slimgears.slimorm.internal.sql.sqlite.SqliteSyntaxProvider;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public class SqliteOrmService extends AbstractSqlOrmService {
    public static final SqlOrmService INSTANCE = new SqliteOrmService();

    private static SqlStatementBuilder createSqlBuilder() {
        SqlStatementBuilder.SyntaxProvider syntaxProvider = new SqliteSyntaxProvider();
        SqlStatementBuilder.PredicateBuilder predicateBuilder = new SqlPredicateBuilder(syntaxProvider);
        return new SqlStatementBuilderImpl(predicateBuilder, syntaxProvider);
    }

    public SqliteOrmService() {
        super(createSqlBuilder());
    }

    @Override
    public <TSession extends RepositorySession> SqlCommandExecutor createCommandExecutor(Repository<TSession> repository, TSession session) {
        SqliteDatabaseProvider dbProvider = (SqliteDatabaseProvider)session;
        return new SqliteCommandExecutor(dbProvider.getDatabase());
    }
}
