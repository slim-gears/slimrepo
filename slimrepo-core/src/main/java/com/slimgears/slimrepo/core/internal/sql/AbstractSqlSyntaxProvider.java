// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

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
    public <T> String substituteParameter(SqlCommand.Parameters params, Field<?, T> field, T value) {
        int index = params.getCount();
        return parameterReference(index, params.add(valueToString(field, value)));
    }

    @Override
    public String qualifiedFieldName(Field<?, ?> field) {
        return tableName(field.metaInfo().getEntityType()) + '.' + simpleFieldName(field);
    }

    @Override
    public String fieldAlias(Field<?, ?> field) {
        return '`' + rawFieldAlias(field) + '`';
    }
}
