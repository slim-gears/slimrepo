// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import com.squareup.javapoet.TypeName;
import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by ditskovi on 12/24/2015.
 */
public class FieldPropertyInfo extends PropertyInfo {
    private final VariableElement element;
    private final TypeName type;

    public FieldPropertyInfo(Elements elementUtils, VariableElement element) {
        this(elementUtils, element, TypeUtils.getTypeName(element.asType()));
    }

    private FieldPropertyInfo(Elements elementUtils, VariableElement element, TypeName type) {
        super(elementUtils);
        this.element = element;
        this.type = type;
    }

    public FieldPropertyInfo withType(TypeName type) {
        return new FieldPropertyInfo(elementUtils, element, type);
    }

    @Override
    public String getName() {
        return element.getSimpleName().toString();
    }

    @Override
    public TypeName getType() {
        return type;
    }

    @Override
    public String getSetterName() {
        return TypeUtils.toCamelCase("set", getName());
    }

    @Override
    public String getGetterName() {
        return TypeUtils.toCamelCase("get", getName());
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return element != null ? element.getAnnotation(annotationClass) : null;
    }

    @Override
    public TypeElement getTypeElement() {
        return elementUtils.getTypeElement(element.asType().toString());
    }

    public boolean requiresTypeCasting() {
        return element != null && !element.asType().toString().equals(type.toString());
    }
}
