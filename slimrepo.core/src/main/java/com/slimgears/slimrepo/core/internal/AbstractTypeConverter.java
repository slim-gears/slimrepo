// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
* Created by Denis on 30-Apr-15
* <File Description>
*/
public abstract class AbstractTypeConverter<TSource, TDestination> implements FieldTypeMappingRegistrar.TypeConverter<TSource> {
    protected final Class<TSource> sourceType;
    protected final Class<TDestination> destinationType;

    protected AbstractTypeConverter(Class<TSource> sourceType, Class<TDestination> destinationType) {
        this.sourceType = sourceType;
        this.destinationType = destinationType;
    }

    protected abstract TSource fromInbound(TDestination value);
    protected abstract TDestination toOutbound(TSource value);

    @Override
    public TSource toEntityType(Field<?, TSource> field, Object value) {
        //noinspection unchecked
        return fromInbound((TDestination)value);
    }

    @Override
    public Object fromEntityType(Field<?, TSource> field, TSource value) {
        return toOutbound(value);
    }

    @Override
    public Class getOutboundType(Field<?, TSource> field) {
        return destinationType;
    }

    @Override
    public Class getInboundType(Field<?, TSource> field) {
        return getOutboundType(field);
    }

    public void install(FieldTypeMappingRegistrar registrar) {
        registrar.registerConverter(sourceType, this);
    }
}
