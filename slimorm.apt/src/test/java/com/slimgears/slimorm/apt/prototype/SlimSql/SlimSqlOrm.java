// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.slimsql;

import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.Repository;
import com.slimgears.slimorm.interfaces.RepositorySession;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.internal.sql.AbstractSqlOrmService;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutor;
import com.slimgears.slimorm.internal.sql.SqlCommandExecutorFactory;
import com.slimgears.slimorm.internal.sql.SqlOrmService;
import com.slimgears.slimorm.internal.sql.SqlPredicateBuilder;
import com.slimgears.slimorm.internal.sql.SqlStatementBuilder;
import com.slimgears.slimorm.internal.sql.DefaultSqlStatementBuilder;

/**
 * Created by Denis on 06-Apr-15
 * <File Description>
 */
public class SlimSqlOrm extends AbstractSqlOrmService {
    public static final SqlOrmService INSTANCE = new SlimSqlOrm();

    private static SqlCommandExecutorFactory commandExecutorFactory = null;

    static class SyntaxProvider implements SqlStatementBuilder.SyntaxProvider {
        @Override
        public String fieldName(Field field) {
            return '\'' + field.getName() + '\'';
        }

        @Override
        public String tableName(EntityType entityType) {
            return '\'' + entityType.getName() + '\'';
        }

        @Override
        public String parameterReference(int index, String name) {
            return "?";
        }

        @Override
        public String valueToString(Object value) {
            return String.valueOf(value);
        }
    }

    private static SqlStatementBuilder createSqlBuilder() {
        SqlStatementBuilder.SyntaxProvider syntaxProvider = new SyntaxProvider();
        SqlStatementBuilder.PredicateBuilder predicateBuilder = new SqlPredicateBuilder(syntaxProvider);
        return new DefaultSqlStatementBuilder(predicateBuilder, syntaxProvider);
    }

    public SlimSqlOrm() {
        super(createSqlBuilder());
    }

    public static void setCommandExecutorFactory(SqlCommandExecutorFactory commandExecutorFactory) {
        SlimSqlOrm.commandExecutorFactory = commandExecutorFactory;
    }

    @Override
    public <TSession extends RepositorySession> SqlCommandExecutor createCommandExecutor(Repository<TSession> repository, TSession session) {
        return commandExecutorFactory != null
                ? commandExecutorFactory.createCommandExecutor(repository, session)
                : null;
    }
}
