// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

/**
 * Created by ditskovi on 12/26/2015.
 */
public abstract class PropertyInfo {
    public abstract String getName();
    public abstract TypeName getType();
    public abstract String getSetterName();
    public abstract String getGetterName();
    public abstract <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    public boolean isNullable() {
        return !getType().isPrimitive();
    }
}
