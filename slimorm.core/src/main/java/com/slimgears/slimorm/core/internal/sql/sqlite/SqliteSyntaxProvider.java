// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql.sqlite;

import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.internal.sql.SqlStatementBuilder;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class SqliteSyntaxProvider implements SqlStatementBuilder.SyntaxProvider {
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
