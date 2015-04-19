// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql.sqlite;

import com.slimgears.slimorm.core.interfaces.entities.EntityType;
import com.slimgears.slimorm.core.interfaces.fields.Field;
import com.slimgears.slimorm.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimorm.core.internal.sql.AbstractSqlSyntaxProvider;
import com.slimgears.slimorm.core.internal.sql.SqlStatementBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class SqliteSyntaxProvider extends AbstractSqlSyntaxProvider {
    private static final Map<Class, String> CLASS_TO_TYPE_NAME_MAP = new HashMap<>();

    static {
        registerType("INTEGER", int.class, Integer.class, short.class, Short.class, long.class, Long.class, Date.class);
        registerType("REAL", float.class, Float.class, double.class, Double.class);
        registerType("TEXT", String.class);
    }

    private static void registerType(String typeName, Class... classes) {
        for (Class c : classes) {
            CLASS_TO_TYPE_NAME_MAP.put(c, typeName);
        }
    }

    @Override
    public String fieldName(Field field) {
        return '`' + field.metaInfo().getName() + '`';
    }

    @Override
    public String tableName(EntityType entityType) {
        return '`' + entityType.getName() + '`';
    }

    @Override
    public <TEntity, T> String typeName(Field<TEntity, T> field) {
        String name = CLASS_TO_TYPE_NAME_MAP.get(field.metaInfo().getType());
        return name != null ? name : "";
    }

    @Override
    public String databaseName(RepositoryModel repositoryModel) {
        return repositoryModel.getName() + ".db";
    }

    @Override
    public String parameterReference(int index, String name) {
        return "?";
    }

    @Override
    public String valueToString(Object value) {
        if (value == null) return "NULL";
        return value.toString();
    }
}
