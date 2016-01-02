// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.converters;

import java.io.Serializable;

/**
 * Created by ditskovi on 12/31/2015.
 */
public abstract class AbstractSerializableTypeConverter<TSource, TDestination extends Serializable> extends AbstractSpecificTypeConverter<TSource, byte[]> {
    private final Class<TDestination> destinationType;

    protected AbstractSerializableTypeConverter(Class<TSource> sourceType, Class<TDestination> destinationType) {
        super(sourceType, byte[].class);
        this.destinationType = destinationType;
    }

    @Override
    protected TSource fromInbound(byte[] value) {
        TDestination serializable = destinationType.cast(SerializableTypeConverter.INSTANCE.fromInbound(value));
        return fromSerializable(serializable);
    }

    @Override
    protected byte[] toOutbound(TSource value) {
        TDestination serializable = toSerializable(value);
        return SerializableTypeConverter.INSTANCE.toOutbound(serializable);
    }

    protected abstract TSource fromSerializable(TDestination destination);
    protected abstract TDestination toSerializable(TSource value);
}
