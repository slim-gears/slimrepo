package com.slimgears.slimrepo.apt.base;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
public abstract class ClassGenerator<T extends ClassGenerator<T>> {
    private ProcessingEnvironment processingEnvironment;
    private Elements elementUtils;
    private Types typeUtils;
    private String className;
    private String packageName;
    private TypeName superClass;
    private Collection<TypeName> superInterfaces = new ArrayList<>();
    private ClassName typeName;

    protected ClassGenerator(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
        this.elementUtils = processingEnvironment.getElementUtils();
        this.typeUtils = processingEnvironment.getTypeUtils();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T)this;
    }

    protected String getClassName() {
        return className;
    }

    protected String getPackageName() {
        return packageName;
    }

    protected ClassName getTypeName() {
        return typeName;
    }

    public TypeName build() throws IOException {
        TypeSpec.Builder typeSpecBuilder = TypeSpec
                .classBuilder(className)
                .superclass(superClass);

        TypeElement[] interfaces = new TypeElement[superInterfaces.size()];
        int index = 0;
        for (TypeName superInterface : superInterfaces) {
            typeSpecBuilder.addSuperinterface(superInterface);
            interfaces[index++] = elementUtils.getTypeElement(superInterface.toString());
        }

        build(typeSpecBuilder, elementUtils.getTypeElement(superClass.toString()), interfaces);

        TypeSpec typeSpec = typeSpecBuilder.build();

        JavaFile javaFile = JavaFile
                .builder(packageName, typeSpec)
                .indent("    ")
                .build();
        javaFile.writeTo(processingEnvironment.getFiler());

        return typeName;
    }

    protected abstract void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces);

    public T className(String qualifiedClassName) {
        return className(TypeUtils.packageName(qualifiedClassName), TypeUtils.simpleName(qualifiedClassName));
    }

    public T className(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.typeName = ClassName.get(packageName, className);
        return self();
    }

    public T addInterfaces(Type... interfaces) {
        return addInterfaces(toTypeNames(interfaces));
    }

    public T addInterfaces(TypeElement... interfaces) {
        return addInterfaces(toTypeNames(interfaces));
    }

    public T addInterfaces(TypeName... typeNames) {
        superInterfaces.addAll(Arrays.asList(typeNames));
        return self();
    }

    public T superClass(TypeName superClass) {
        this.superClass = superClass;
        return self();
    }

    public final T superClass(TypeElement clazz) {
        return superClass(TypeName.get(clazz.asType()));
    }

    public final T superClass(Type clazz) {
        return superClass(TypeName.get(clazz));
    }

    protected Modifier[] toModifiersArray(Collection<Modifier> modifiers) {
        return modifiers.toArray(new Modifier[modifiers.size()]);
    }

    protected TypeName[] toTypeNames(Type[] classes) {
        return Collections2
                .transform(Arrays.asList(classes), TypeName::get)
                .toArray(new TypeName[classes.length]);
    }

    protected TypeName[] toTypeNames(TypeElement[] typeElements) {
        return Collections2
                .transform(Arrays.asList(typeElements), input -> TypeName.get(input.asType()))
                .toArray(new TypeName[typeElements.length]);
    }

    protected ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }

    protected Elements getElementUtils() {
        return elementUtils;
    }

    protected Types getTypeUtils() {
        return typeUtils;
    }
}
