package com.slimgears.slimorm.apt.base;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
public abstract class ClassGenerator<T extends ClassGenerator<T>> {
    private TypeSpec.Builder typeSpecBuilder;
    private String className;
    private String packageName;
    private TypeElement superClass;
    private List<TypeElement> superInterfaces = new ArrayList<>();
    private TypeName typeName;

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

    protected TypeName getTypeName() {
        return typeName;
    }

    public TypeName build(Filer filer) throws IOException {
        typeSpecBuilder = TypeSpec
                .classBuilder(className)
                .superclass(TypeName.get(superClass.asType()));

        for (TypeElement superInterface : superInterfaces) {
            typeSpecBuilder.addSuperinterface(TypeName.get(superInterface.asType()));
        }

        TypeElement[] interfaces = superInterfaces.toArray(new TypeElement[superInterfaces.size()]);
        build(typeSpecBuilder, superClass, interfaces);

        TypeSpec typeSpec = typeSpecBuilder.build();
        typeSpecBuilder = null;

        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(filer);

        return typeName;
    }

    protected abstract void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces);

    public T className(String qualifiedClassName) {
        int pos = qualifiedClassName.lastIndexOf('.');
        String simpleName = qualifiedClassName.substring(pos + 1);
        String packageName = (pos >= 0) ? qualifiedClassName.substring(0, pos) : "";
        return className(packageName, simpleName);
    }

    public T className(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        this.typeName = ClassName.get(packageName, className);
        return self();
    }

    public T addInterfaces(TypeElement... interfaces) {
        superInterfaces.addAll(Arrays.asList(interfaces));
        return self();
    }

    public T superClass(TypeElement clazz) {
        superClass = clazz;
        return self();
    }

    protected Modifier[] toModifiersArray(Collection<Modifier> modifiers) {
        return modifiers.toArray(new Modifier[modifiers.size()]);
    }
}
