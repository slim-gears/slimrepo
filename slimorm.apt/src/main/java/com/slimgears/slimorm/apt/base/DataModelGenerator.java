package com.slimgears.slimorm.apt.base;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Denis on 04-Apr-15
 * <File Description>
 */
public class DataModelGenerator extends ClassGenerator<DataModelGenerator> {
    private TypeSpec.Builder builderClassBuilder;
    private TypeName builderTypeName;

    class Visitor extends ElementVisitorBase<Void, Void> {
        private final TypeSpec.Builder modelBuilder;
        private MethodSpec.Builder modelCtorBuilder;

        public Visitor(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder) {
            this.modelBuilder = modelBuilder;
            this.modelCtorBuilder = modelCtorBuilder;
        }

        @Override
        public Void visitVariable(VariableElement variableElement, Void arg) {
            String name = variableElement.getSimpleName().toString();

            TypeName type = TypeName.get(variableElement.asType());

            modelBuilder.addMethod(createModelSetter(name, type, getTypeName()));
            modelBuilder.addMethod(createModelGetter(name, type));
            builderClassBuilder.addMethod(createBuilderSetter(name, type, builderTypeName));
            modelCtorBuilder
                    .addParameter(type, name)
                    .addCode("this.$L = $L;\n", name, name);

            return null;
        }
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        builder.addModifiers(toModifiersArray(type.getModifiers()));

        builderClassBuilder = TypeSpec
                .classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        builderTypeName = ClassName.get(getPackageName(), getClassName(), "Builder");

        TypeName modelType = getTypeName();

        builderClassBuilder.addField(FieldSpec
                .builder(modelType, "model", Modifier.PRIVATE)
                .initializer("new $T()", modelType)
                .build());

        builderClassBuilder.addMethod(MethodSpec
                .methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(modelType)
                .addCode("return model;\n")
                .build());

        builder.addMethod(MethodSpec
                .methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderTypeName)
                .addCode("return new Builder();\n")
                .build());

        builder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

        MethodSpec.Builder modelCtorBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        type.accept(new Visitor(builder, modelCtorBuilder), null);

        builder.addType(builderClassBuilder.build());
        builder.addMethod(modelCtorBuilder.build());
    }

    MethodSpec createModelGetter(String fieldName, TypeName fieldType) {
        return MethodSpec
                .methodBuilder(toCamelCase("get", fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addCode("return this.$L;\n", fieldName)
                .build();
    }

    MethodSpec createModelSetter(String fieldName, TypeName fieldType, TypeName modelType) {
        return MethodSpec
                .methodBuilder(toCamelCase("set", fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(modelType)
                .addCode("this.$L = $L;\n", fieldName, fieldName)
                .addCode("return this;\n")
                .build();
    }

    MethodSpec createBuilderSetter(String fieldName, TypeName fieldType, TypeName builderType) {
        return MethodSpec
                .methodBuilder(fieldName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(builderType)
                .addCode("model.$L($L);\n", toCamelCase("set", fieldName), fieldName)
                .addCode("return this;\n")
                .build();
    }

    String toCamelCase(String begin, String... parts) {
        String name = begin;
        for (String part : parts) {
            name += Character.toUpperCase(part.charAt(0)) + part.substring(1);
        }
        return name;
    }
}
