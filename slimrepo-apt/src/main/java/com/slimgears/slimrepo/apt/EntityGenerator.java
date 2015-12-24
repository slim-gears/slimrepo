// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Joiner;
import com.slimgears.slimrepo.apt.base.DataModelGenerator;
import com.slimgears.slimrepo.apt.base.FieldInfo;
import com.slimgears.slimrepo.apt.base.TypeUtils;
import com.slimgears.slimrepo.core.annotations.BlobSemantics;
import com.slimgears.slimrepo.core.annotations.ComparableSemantics;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.annotations.ValueSemantics;
import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;

/**
 * Created by Denis on 21-Apr-15
 * <File Description>
 */
public class EntityGenerator extends DataModelGenerator {
    public EntityGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    @Override
    public EntityGenerator superClass(TypeName superClass) {
        super.superClass(superClass);
        className(TypeUtils.packageName(superClass.toString()), MetaFields.generateEntityTypeName(superClass));
        return this;
    }

    @Override
    protected TypeSpec.Builder createModelBuilder(String name) {
        return super.createModelBuilder(name)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(EntityBuilder.class), ClassName.get(getPackageName(), getClassName())));
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, List<FieldInfo> fields) {
        MetaFields metaFields = new MetaFields(getProcessingEnvironment(), type);

        FieldInfo keyField = getKeyField(fields);
        fields.remove(keyField);
        fields.add(0, keyField);

        TypeName keyType = MetaFields.box(keyField.type);
        ClassName entityType = getTypeName();

        for (FieldInfo field : fields) {
            builder.addField(metaFields.buildMetaField(entityType, field));
        }

        builder
            .addField(FieldSpec
                .builder(ParameterizedTypeName.get(ClassName.get(EntityType.class), keyType, entityType), "EntityMetaType", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new MetaType()")
                .build())
            .addType(createMetaType(keyType, fields));

        super.build(builder, type, fields);
    }

    private TypeSpec createMetaType(TypeName keyType, Iterable<FieldInfo> fields) {
        ClassName entityType = getTypeName();

        return TypeSpec.classBuilder("MetaType")
            .superclass(ParameterizedTypeName.get(ClassName.get(AbstractEntityType.class), keyType, getTypeName()))
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .addMethod(MethodSpec.constructorBuilder()
                .addCode("super($T.class, ", getTypeName())
                .addCode(Joiner
                        .on(", ")
                        .join(transform(fields, field -> MetaFields.getMetaFieldName(field.name))))
                .addCode(");\n")
                .build())
            .addMethod(MethodSpec.methodBuilder("newInstance")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(entityType)
                .addCode("return new $T();\n", entityType)
                .build())
            .build();
    }

    @Override
    protected FieldInfo createFieldInfo(VariableElement element) {
        FieldInfo field = super.createFieldInfo(element);
        TypeElement typeElement = getElementUtils().getTypeElement(field.type.toString());
        if (typeElement != null && typeElement.getAnnotation(GenerateEntity.class) != null) {
            return field.withType(MetaFields.entityTypeFromAbstract(field.type));
        }
        return field;
    }

    private FieldInfo getKeyField(Iterable<FieldInfo> fields) {
        FieldInfo field = findAnnotatedField(fields, Key.class);
        return field != null ? field : findFieldByName(fields, "id", getEntityIdFieldName());
    }

    private String getEntityIdFieldName() {
        return TypeUtils.toCamelCase(getClassName().replace("Entity", ""), "Id");
    }

    private FieldInfo findFieldByName(Iterable<FieldInfo> fields, String... names) {
        final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
        return find(fields, field -> nameSet.contains(field.name));
    }

    private FieldInfo findAnnotatedField(Iterable<FieldInfo> fields, final Class annotationClass) {
        return find(fields, input -> input.getAnnotation(annotationClass) != null, null);
    }
}
