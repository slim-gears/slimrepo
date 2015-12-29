package com.slimgears.slimrepo.apt.base;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
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

    public DataModelGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    protected TypeSpec.Builder createModelBuilder(String name) {
        return TypeSpec
                .classBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
    }

    protected void build(TypeSpec.Builder builder, TypeElement type, List<FieldPropertyInfo> fields) {
        builder.addModifiers(toModifiersArray(type.getModifiers()));

        String modelBuilderName = "Builder";
        builderClassBuilder = createModelBuilder(modelBuilderName);
        builderTypeName = ClassName.get(getPackageName(), getClassName(), modelBuilderName);

        TypeName modelType = getTypeName();

        builderClassBuilder
                .addField(FieldSpec
                        .builder(modelType, "model", Modifier.PRIVATE)
                        .initializer("new $T()", modelType)
                        .build())

                .addMethod(MethodSpec
                        .methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(modelType)
                        .addCode("return model;\n")
                        .build());

        builder
                .addMethod(MethodSpec
                        .methodBuilder("builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(builderTypeName)
                        .addCode("return new $L();\n", modelBuilderName)
                        .build())
                .addMethod(MethodSpec
                        .methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(modelType)
                        .addCode("return new $T();\n", modelType)
                        .build())
                .addMethod(MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build());

        MethodSpec.Builder modelCtorBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        processFields(builder, modelCtorBuilder, fields);

        builder.addType(builderClassBuilder.build());
        builder.addMethod(modelCtorBuilder.build());
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        final List<FieldPropertyInfo> fields = new ArrayList<>();

        type.accept(new ElementVisitorBase<Void, Void>(){
            @Override
            public Void visitVariable(VariableElement element, Void param) {
                fields.add(createFieldInfo(element));
                return null;
            }
        }, null);

        build(builder, type, fields);
    }

    protected FieldPropertyInfo createFieldInfo(VariableElement element) {
        return new FieldPropertyInfo(getElementUtils(), element);
    }

    protected void processFields(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder, Iterable<FieldPropertyInfo> fields) {
        for (FieldPropertyInfo field : fields) {
            processField(modelBuilder, modelCtorBuilder, field);
        }
    }

    protected void processField(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder, FieldPropertyInfo field) {
        modelBuilder.addMethod(createModelSetter(field, getTypeName()));
        modelBuilder.addMethod(createModelGetter(field));
        builderClassBuilder.addMethod(createBuilderSetter(field, builderTypeName));
        modelCtorBuilder
                .addParameter(field.getType(), field.getName())
                .addCode("this.$L = $L;\n", field.getName(), field.getName());
    }

    protected MethodSpec createModelGetter(FieldPropertyInfo field) {
        MethodSpec.Builder builder = MethodSpec
                .methodBuilder(field.getGetterName())
                .addModifiers(Modifier.PUBLIC)
                .returns(field.getType());

        return (field.requiresTypeCasting())
                ? builder.addCode("return ($T)this.$L;\n", field.getType(), field.getName()).build()
                : builder.addCode("return this.$L;\n", field.getName()).build();
    }

    protected MethodSpec createModelSetter(FieldPropertyInfo field, TypeName modelType) {
        return MethodSpec
                .methodBuilder(field.getSetterName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(field.getType(), field.getName())
                .returns(modelType)
                .addCode("this.$L = $L;\n", field.getName(), field.getName())
                .addCode("return this;\n")
                .build();
    }

    MethodSpec createBuilderSetter(FieldPropertyInfo field, TypeName builderType) {
        return MethodSpec
                .methodBuilder(field.getName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(field.getType(), field.getName())
                .returns(builderType)
                .addCode("model.$L($L);\n", field.getSetterName(), field.getName())
                .addCode("return this;\n")
                .build();
    }
}
