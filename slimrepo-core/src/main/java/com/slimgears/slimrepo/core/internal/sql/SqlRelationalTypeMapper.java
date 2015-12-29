// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.TypeConverter;

/**
 * Created by ditskovi on 4/27/2015.
 */
class SqlRelationalTypeMapper implements FieldTypeMappingRegistrar.Matcher, TypeConverter {
    @Override
    public boolean match(Field field) {
        return field instanceof RelationalField;
    }

    @Override
    public Object toEntityType(Field field, Object value) {
        if (value == null) return null;

        RelationalField relationalField = (RelationalField)field;
        EntityType relatedEntityType = relationalField.metaInfo().getRelatedEntityType();
        //noinspection unchecked
        return relatedEntityType.newInstance((FieldValueLookup)value);
    }

    @Override
    public Object fromEntityType(Field field, Object value) {
        if (value == null) return null;
        //noinspection unchecked

        RelationalField relationalField = (RelationalField)field;
        EntityType relatedEntityType = relationalField.metaInfo().getRelatedEntityType();

        //noinspection unchecked
        return relatedEntityType.getKey(value);
    }

    @Override
    public Class getOutboundType(Field field) {
        RelationalField relationalField = (RelationalField)field;
        return relationalField.metaInfo().getRelatedEntityType().getKeyField().metaInfo().getValueType();
    }

    @Override
    public Class getInboundType(Field field) {
        return FieldValueLookup.class;
    }
}
