// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlSyntaxProvider implements SqlStatementBuilder.SyntaxProvider {
    protected final FieldTypeMapper fieldTypeMapper;

    protected AbstractSqlSyntaxProvider(OrmServiceProvider ormServiceProvider) {
        fieldTypeMapper = ormServiceProvider.getFieldTypeMapper();
    }

    @Override
    public <TEntity, T> String substituteParameter(SqlCommand.Parameters params, Field<TEntity, T> field, T value) {
        int index = params.getCount();
        return parameterReference(index, params.add(valueToString(field, value)));
    }
}
