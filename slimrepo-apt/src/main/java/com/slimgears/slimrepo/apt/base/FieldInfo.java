// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by ditskovi on 12/24/2015.
 */
public class FieldInfo {
    private final VariableElement element;

    public final String name;
    public final TypeName type;
    public final String getterName;
    public final String setterName;

    public FieldInfo(VariableElement element) {
        this.name = element.getSimpleName().toString();
        this.type = getTypeName(element.asType());
        this.getterName = TypeUtils.toCamelCase("get", name);
        this.setterName = TypeUtils.toCamelCase("set", name);
        this.element = element;
    }

    public FieldInfo(ExecutableElement getterElement, ExecutableElement setterElement) {
        this.getterName = getterElement.getSimpleName().toString();
        this.setterName = setterElement.getSimpleName().toString();
        this.name = this.setterName.startsWith("set") ? this.setterName.substring(3) : this.setterName;
        this.type = getTypeName(getterElement.getReturnType());
        this.element = null;
    }

    private FieldInfo(String name, TypeName type, String getterName, String setterName, VariableElement element) {
        this.name = name;
        this.type = type;
        this.getterName = getterName;
        this.setterName = setterName;
        this.element = element;
    }

    public FieldInfo withType(TypeName type) {
        return new FieldInfo(name, type, getterName, setterName, element);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return element != null ? element.getAnnotation(annotationClass) : null;
    }

    public boolean requiresTypeCasting() {
        return element != null && !element.asType().toString().equals(type.toString());
    }

    public boolean isNullable() {
        return !type.isPrimitive();
    }

    private static TypeName getTypeName(final TypeMirror typeMirror) {
        try {
            return TypeName.get(typeMirror);
        } catch (Throwable e) {
            return ClassName.get(TypeUtils.packageName(typeMirror.toString()), TypeUtils.simpleName(typeMirror.toString()));
        }
    }
}
