// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.slimgears.slimapt.DataModelGenerator;
import com.slimgears.slimapt.FieldPropertyInfo;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityBuilder;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

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
        className(
                TypeUtils.packageName(superClass.toString()),
                MetaFields.generatedEntityTypeName(superClass));
        return this;
    }

    @Override
    protected TypeSpec.Builder createModelBuilder(String name) {
        return super.createModelBuilder(name)
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(EntityBuilder.class),
                        ClassName.get(getPackageName(), getClassName())));
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, List<FieldPropertyInfo> fields) {
        MetaFields metaFields = new MetaFields(type);

        FieldPropertyInfo keyField = MetaFields.getKeyField(getTypeName(), fields);
        fields.remove(keyField);
        fields.add(0, keyField);

        TypeName keyType = TypeUtils.box(keyField.getType());
        ClassName entityType = getTypeName();

        for (FieldPropertyInfo field : fields) {
            builder.addField(metaFields.buildMetaField(entityType, field));
        }

        builder
                .addAnnotation(AnnotationSpec
                        .builder(Generated.class)
                        .addMember("value", "\"" + type.asType().toString() + "\"")
                        .addMember("comments", "\"Entity generated from " + type.asType().toString() + "\"")
                        .build())
                .addField(FieldSpec
                        .builder(ParameterizedTypeName.get(
                                ClassName.get(EntityType.class), keyType, entityType),
                                "EntityMetaType",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new MetaType()")
                        .build())
                .addType(MetaFields.createMetaType(getTypeName(), keyType, fields));

        super.build(builder, type, fields);
    }


    @Override
    protected FieldPropertyInfo createFieldInfo(VariableElement element) {
        FieldPropertyInfo field = super.createFieldInfo(element);
        TypeElement typeElement = getElementUtils().getTypeElement(field.getType().toString());
        if (typeElement != null && typeElement.getAnnotation(GenerateEntity.class) != null) {
            return field.withType(MetaFields.entityTypeFromAbstract(field.getType()));
        }
        return field;
    }
}
