// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public class SqlLazyCommand implements SqlCommand {
    private final SqlStatementBuilder sqlBuilder;
    private final CommandBuilder commandBuilder;
    private String statement;
    private Parameters parameters;

    public interface CommandBuilder {
        String buildCommand(SqlStatementBuilder sqlBuilder, Parameters parameters);
    }

    public SqlLazyCommand(SqlStatementBuilder sqlBuilder, CommandBuilder commandBuilder) {
        this.sqlBuilder = sqlBuilder;
        this.commandBuilder = commandBuilder;
    }

    @Override
    public String getStatement() {
        ensureCommandWasBuilt();
        return statement;
    }

    @Override
    public Parameters getParameters() {
        ensureCommandWasBuilt();
        return parameters;
    }

    private void ensureCommandWasBuilt() {
        if (statement == null || parameters == null) {
            parameters = new SqlCommandParameters();
            statement = commandBuilder.buildCommand(sqlBuilder, parameters);
        }
    }
}
