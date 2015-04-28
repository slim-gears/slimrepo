// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlOrmServiceProvider;
import com.slimgears.slimrepo.core.internal.sql.SqlStatementBuilder;

import java.util.Date;

/**
 * Created by Denis on 15-Apr-15
 * <File Description>
 */
public abstract class AbstractSqliteOrmServiceProvider extends AbstractSqlOrmServiceProvider {
    protected AbstractSqliteOrmServiceProvider() {
        registerConverter(Date.class, new FieldTypeMappingRegistrar.TypeConverter<Date>() {
            @Override
            public Date toEntityType(Field<?, Date> field, Object value) {
                return value != null ? new Date((Long)value) : null;
            }

            @Override
            public Object fromEntityType(Field<?, Date> field, Date value) {
                return value != null ? value.getTime() : null;
            }

            @Override
            public Class getOutboundType(Field<?, Date> field) {
                return Long.class;
            }

            @Override
            public Class getInboundType(Field<?, Date> field) {
                return Long.class;
            }
        });
    }

    @Override
    protected SqlStatementBuilder.SyntaxProvider createSyntaxProvider() {
        return new SqliteSyntaxProvider(this);
    }
}
