// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by ditskovi on 12/26/2015.
 */
public abstract class PropertyInfo {
    protected final Elements elementUtils;

    protected PropertyInfo(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    public abstract String getName();
    public abstract TypeName getType();
    public abstract String getSetterName();
    public abstract String getGetterName();
    public abstract <A extends Annotation> A getAnnotation(Class<A> annotationClass);
    public abstract TypeElement getTypeElement();

    public boolean isNullable() {
        return !getType().isPrimitive();
    }
}
