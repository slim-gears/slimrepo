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

    protected class FieldInfo {
        public final VariableElement element;
        public final String name;
        public final TypeName type;

        FieldInfo(VariableElement element) {
            this.element = element;
            this.name = element.getSimpleName().toString();
            this.type = TypeName.get(element.asType());
        }
    }

    public DataModelGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    protected void build(TypeSpec.Builder builder, TypeElement type, List<FieldInfo> fields) {
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

        processFields(builder, modelCtorBuilder, fields);

        builder.addType(builderClassBuilder.build());
        builder.addMethod(modelCtorBuilder.build());
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        final List<FieldInfo> fields = new ArrayList<>();

        type.accept(new ElementVisitorBase<Void, Void>(){
            @Override
            public Void visitVariable(VariableElement element, Void param) {
                fields.add(new FieldInfo(element));
                return null;
            }
        }, null);

        build(builder, type, fields);
    }

    protected void processFields(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder, Iterable<FieldInfo> fields) {
        for (FieldInfo field : fields) {
            processField(modelBuilder, modelCtorBuilder, field);
        }
    }

    protected void processField(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder, FieldInfo field) {
        modelBuilder.addMethod(createModelSetter(field.name, field.type, getTypeName()));
        modelBuilder.addMethod(createModelGetter(field.name, field.type));
        builderClassBuilder.addMethod(createBuilderSetter(field.name, field.type, builderTypeName));
        modelCtorBuilder
                .addParameter(field.type, field.name)
                .addCode("this.$L = $L;\n", field.name, field.name);
    }

    private MethodSpec createModelGetter(String fieldName, TypeName fieldType) {
        return MethodSpec
                .methodBuilder(getModelGetterName(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addCode("return this.$L;\n", fieldName)
                .build();
    }

    private MethodSpec createModelSetter(String fieldName, TypeName fieldType, TypeName modelType) {
        return MethodSpec
                .methodBuilder(getModelSetterName(fieldName))
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
                .addCode("model.$L($L);\n", getModelSetterName(fieldName), fieldName)
                .addCode("return this;\n")
                .build();
    }

    protected String getModelGetterName(String fieldName) {
        return toCamelCase("get", fieldName);
    }

    protected String getModelSetterName(String fieldName) {
        return toCamelCase("set", fieldName);
    }
}
