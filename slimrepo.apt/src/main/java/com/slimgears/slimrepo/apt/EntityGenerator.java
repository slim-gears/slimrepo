// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import com.slimgears.slimrepo.apt.base.DataModelGenerator;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.Fields;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.google.common.collect.Iterables.find;

/**
 * Created by Denis on 21-Apr-15
 * <File Description>
 */
public class EntityGenerator extends DataModelGenerator {
    private static final Map<TypeName, AbstractMetaFieldBuilder> META_FIELD_BUILDER_MAP = new HashMap<>();

    static {
        META_FIELD_BUILDER_MAP.put(TypeName.INT, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.SHORT, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.LONG, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.BYTE, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.DOUBLE, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.FLOAT, NumericMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.get(Date.class), DateMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.get(String.class), StringMetaFieldBuilder.INSTANCE);
    }

    private static FieldSpec buildMetaField(ClassName entityType, String name, VariableElement field) {
        AbstractMetaFieldBuilder builder = META_FIELD_BUILDER_MAP.get(TypeName.get(field.asType()));
        if (builder == null) builder = BlobMetaFieldBuilder.INSTANCE;
        return builder.build(entityType, name, field);
    }

    static abstract class AbstractMetaFieldBuilder {
        public FieldSpec build(ClassName entityType, String name, VariableElement field) {
            TypeName fieldType = TypeName.get(field.asType());
            TypeName type = metaFieldType(entityType, fieldType);
            FieldSpec.Builder builder = FieldSpec.builder(type, name, Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
            return initialize(builder, entityType, fieldType, field.getSimpleName().toString(), !fieldType.isPrimitive()).build();
        }

        protected abstract TypeName metaFieldType(ClassName entityType, TypeName fieldType);
        protected abstract FieldSpec.Builder initialize(FieldSpec.Builder builder, TypeName entityType, TypeName fieldType, String fieldName, boolean isNullable);
    }

    static class NumericMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final NumericMetaFieldBuilder INSTANCE = new NumericMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(NumericField.class), entityType, box(fieldType));
        }

        @Override
        protected FieldSpec.Builder initialize(FieldSpec.Builder builder, TypeName entityType, TypeName fieldType, String fieldName, boolean isNullable) {
            return builder.initializer("$T.numberField($S, $T.class, $T.class, $L)", Fields.class, fieldName, entityType, box(fieldType), isNullable);
        }
    }

    static class DateMetaFieldBuilder extends NumericMetaFieldBuilder {
        static final DateMetaFieldBuilder INSTANCE = new DateMetaFieldBuilder();

        @Override
        protected FieldSpec.Builder initialize(FieldSpec.Builder builder, TypeName entityType, TypeName fieldType, String fieldName, boolean isNullable) {
            return builder.initializer("$T.dateField($S, $T.class, $L)", Fields.class, fieldName, entityType, isNullable);
        }
    }

    static class StringMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final StringMetaFieldBuilder INSTANCE = new StringMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(StringField.class), entityType);
        }

        @Override
        protected FieldSpec.Builder initialize(FieldSpec.Builder builder, TypeName entityType, TypeName fieldType, String fieldName, boolean isNullable) {
            return builder.initializer("$T.stringField($S, $T.class, $L)", Fields.class, fieldName, entityType, isNullable);
        }
    }

    static class BlobMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final BlobMetaFieldBuilder INSTANCE = new BlobMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(BlobField.class), entityType, fieldType);
        }

        @Override
        protected FieldSpec.Builder initialize(FieldSpec.Builder builder, TypeName entityType, TypeName fieldType, String fieldName, boolean isNullable) {
            return builder.initializer("$T.blobField($S, $T, $T, $L)", Fields.class, fieldName, entityType, fieldType, isNullable);
        }
    }

    private MethodSpec.Builder metaNewInstanceMethodBuilder;
    private MethodSpec.Builder metaEntityToMapMethodBuilder;

    public EntityGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    class Visitor extends DataModelGenerator.Visitor {
        public Visitor(TypeSpec.Builder modelBuilder, MethodSpec.Builder modelCtorBuilder) {
            super(modelBuilder, modelCtorBuilder);
        }

        @Override
        public Void visitVariable(VariableElement variableElement, Void arg) {
            String fieldName = variableElement.getSimpleName().toString();
            String metaFieldName = getFieldMetaName(fieldName);
            ClassName entityType = getTypeName();
            modelBuilder.addField(buildMetaField(entityType, metaFieldName, variableElement));

            if (metaNewInstanceMethodBuilder == null) {
                metaNewInstanceMethodBuilder = MethodSpec.methodBuilder("newInstance")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(entityType)
                        .addParameter(ParameterizedTypeName.get(ClassName.get(FieldValueLookup.class), entityType), "lookup")
                        .addCode("return new $T(\n", entityType)
                        .addCode("    lookup.getValue($L)", metaFieldName);

                metaEntityToMapMethodBuilder = MethodSpec.methodBuilder("entityToMap")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(entityType, "entity")
                        .addParameter(ParameterizedTypeName.get(ClassName.get(FieldValueMap.class), entityType), "map")
                        .addCode("map\n")
                        .addCode("    .putValue($L, entity.$L())", metaFieldName, getModelGetterName(fieldName));
            } else {
                metaNewInstanceMethodBuilder.addCode(",\n    lookup.getValue($L)", metaFieldName);
                metaEntityToMapMethodBuilder.addCode("\n    .putValue($L, entity.$L())", metaFieldName, getModelGetterName(fieldName));
            }

            return super.visitVariable(variableElement, arg);
        }
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        VariableElement keyField = getKeyField(type);
        TypeName keyType = box(TypeName.get(keyField.asType()));
        TypeName entityType = getTypeName();

        TypeSpec.Builder metaTypeBuilder = createMetaTypeBuilder(keyField, keyType);

        builder.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Entity.class), keyType));
        builder.addMethod(MethodSpec.methodBuilder("getEntityId")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(keyType)
                .addCode("return this.$L;\n", keyField.getSimpleName().toString())
                .build());

        builder.addField(FieldSpec
                .builder(ParameterizedTypeName.get(ClassName.get(EntityType.class), keyType, entityType), "EntityMetaType", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T()", ClassName.get(getPackageName(), getClassName(), "MetaType"))
                .build());

        super.build(builder, type, interfaces);

        metaTypeBuilder.addMethod(metaNewInstanceMethodBuilder.addCode(");\n").build());
        metaTypeBuilder.addMethod(metaEntityToMapMethodBuilder.addCode(";\n").build());

        builder.addType(metaTypeBuilder.build());
    }

    private TypeSpec.Builder createMetaTypeBuilder(VariableElement keyField, TypeName keyType) {
        ClassName entityType = getTypeName();
        String keyFieldName = keyField.getSimpleName().toString();

        TypeSpec.Builder metaTypeBuilder = TypeSpec.classBuilder("MetaType")
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractEntityType.class), keyType, getTypeName()))
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC);

        metaTypeBuilder.addMethod(MethodSpec.constructorBuilder()
                .addCode("super($S, $T.class, $L);\n", getClassName(), getTypeName(), getFieldMetaName(keyFieldName))
                .build());

        metaTypeBuilder.addMethod(MethodSpec.methodBuilder("newInstance")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityType)
                .addCode("return new $T();\n", entityType)
                .build());

        metaTypeBuilder.addMethod(MethodSpec.methodBuilder("setKey")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(entityType, "entity")
                .addParameter(keyType, "key")
                .addCode("entity.$L(key);\n", getModelSetterName(keyFieldName))
                .build());

        return metaTypeBuilder;
    }

    @Override
    protected Visitor createVisitor(TypeSpec.Builder typeBuilder, MethodSpec.Builder ctorBuilder) {
        return new Visitor(typeBuilder, ctorBuilder);
    }

    private String getFieldMetaName(String fieldName) {
        return toCamelCase("", fieldName);
    }

    private VariableElement getKeyField(TypeElement entityBaseType) {
        VariableElement keyField = (VariableElement)find(entityBaseType.getEnclosedElements(), new Predicate<Element>() {
            @Override
            public boolean apply(Element input) {
                return (input instanceof VariableElement) && input.getAnnotation(Key.class) != null;
            }
        });

        if (keyField == null) throw new RuntimeException("Key field not found in class '" + entityBaseType.getQualifiedName() + "'");
        return keyField;
    }

    private static TypeName box(TypeName type) {
        if (type == TypeName.INT) return TypeName.get(Integer.class);
        if (type == TypeName.SHORT) return TypeName.get(Short.class);
        if (type == TypeName.LONG) return TypeName.get(Long.class);
        if (type == TypeName.BOOLEAN) return TypeName.get(Boolean.class);
        if (type == TypeName.DOUBLE) return TypeName.get(Double.class);
        if (type == TypeName.FLOAT) return TypeName.get(Float.class);
        if (type == TypeName.BYTE) return TypeName.get(Byte.class);
        return type;
    }
}
