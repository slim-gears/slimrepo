// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
 * Created by ditskovi on 4/27/2015.
 */
public class SqlRelationalTypeMapper implements FieldTypeMappingRegistrar.Matcher, FieldTypeMappingRegistrar.TypeConverter {
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
        return value != null ? ((Entity)value).getEntityId() : null;
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
