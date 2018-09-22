// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.converters;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
* Created by Denis on 30-Apr-15
*
*/
public abstract class AbstractSpecificTypeConverter<TSource, TDestination>
        extends AbstractTypeConverter<TSource, TDestination>
        implements FieldTypeMappingInstaller {
    protected final Class<TSource> sourceType;

    protected AbstractSpecificTypeConverter(Class<TSource> sourceType, Class<TDestination> destinationType) {
        super(destinationType);
        this.sourceType = sourceType;
    }

    @Override
    public void install(FieldTypeMappingRegistrar registrar) {
        registrar.registerConverter(sourceType, this);
    }
}
