// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSyntaxProvider;

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
        registerType("BLOB", byte[].class);
    }

    public SqliteSyntaxProvider(OrmServiceProvider ormServiceProvider) {
        super(ormServiceProvider);
    }

    private static void registerType(String typeName, Class... classes) {
        for (Class c : classes) {
            CLASS_TO_TYPE_NAME_MAP.put(c, typeName);
        }
    }

    @Override
    public String simpleFieldName(Field field) {
        return '`' + field.metaInfo().getName() + '`';
    }

    @Override
    public String tableName(EntityType entityType) {
        return '`' + entityType.getName() + '`';
    }

    @Override
    public String typeName(Field<?, ?> field) {
        Class mappedType = fieldTypeMapper.getOutboundType(field);
        String name = CLASS_TO_TYPE_NAME_MAP.get(mappedType);
        if (name == null) {
            throw new RuntimeException("Field type " + mappedType.getSimpleName() + " is not supported");
        }
        return name;
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
    public <T> String valueToString(Field<?, T> field, T value) {
        if (value == null) return "NULL";
        return fieldTypeMapper.fromFieldType(field, value).toString();
    }

    @Override
    public String rawFieldAlias(Field<?, ?> field) {
        Field.MetaInfo metaInfo = field.metaInfo();
        return metaInfo.getEntityType().getName() + "_" + metaInfo.getName();
    }
}
