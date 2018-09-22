// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.interfaces;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 24-Apr-15
 *
 */
public interface FieldTypeMapper {
    <T> T toFieldType(Field<?, T> field, Object value);
    <T> Object fromFieldType(Field<?, T> field, T value);
    <T> Class getOutboundType(Field<?, T> field);
    <T> Class getInboundType(Field<?, T> field);
}
