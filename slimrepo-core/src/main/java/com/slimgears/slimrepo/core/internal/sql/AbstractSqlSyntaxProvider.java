// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMapper;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlCommand;
import com.slimgears.slimrepo.core.internal.sql.interfaces.SqlStatementBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Denis on 18-Apr-15
 * <File Description>
 */
public abstract class AbstractSqlSyntaxProvider implements SqlStatementBuilder.SyntaxProvider {
    private static final Map<Class, String> CLASS_TO_TYPE_NAME_MAP = new HashMap<>();

    static {
        registerType("INTEGER", int.class, Integer.class, short.class, Short.class, long.class, Long.class);
        registerType("REAL", float.class, Float.class, double.class, Double.class);
        registerType("TEXT", String.class);
        registerType("BLOB", byte[].class);
    }

    private static void registerType(String typeName, Class... classes) {
        for (Class c : classes) {
            CLASS_TO_TYPE_NAME_MAP.put(c, typeName);
        }
    }

    protected final FieldTypeMapper fieldTypeMapper;

    protected AbstractSqlSyntaxProvider(FieldTypeMappingRegistrar mappingRegistrar) {
        this.fieldTypeMapper = mappingRegistrar;
        mappingRegistrar.registerNotConvertibleTypes(CLASS_TO_TYPE_NAME_MAP.keySet());
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
