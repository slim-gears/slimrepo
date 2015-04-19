// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlSyntaxProvider implements SqlStatementBuilder.SyntaxProvider {
    @Override
    public String substituteParameter(SqlCommand.Parameters params, Object value) {
        int index = params.getCount();
        return parameterReference(index, params.add(valueToString(value)));
    }
}
