// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by ditskovi on 12/27/2015.
 */
public class GetterSetterPropertyInfo extends PropertyInfo {
    private final String name;
    private final ExecutableElement getterElement;
    private final ExecutableElement setterElement;

    public GetterSetterPropertyInfo(Elements elementUtils, String name, ExecutableElement getterElement, ExecutableElement setterElement) {
        super(elementUtils);
        this.getterElement = getterElement;
        this.setterElement = setterElement;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeName getType() {
        return TypeUtils.getTypeName(getterElement.getReturnType());
    }

    @Override
    public String getSetterName() {
        return setterElement.getSimpleName().toString();
    }

    @Override
    public String getGetterName() {
        return getterElement.getSimpleName().toString();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        A annotation = getterElement.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = setterElement.getAnnotation(annotationClass);
        }
        return annotation;
    }

    @Override
    public TypeElement getTypeElement() {
        return elementUtils.getTypeElement(getterElement.getReturnType().toString());
    }
}
