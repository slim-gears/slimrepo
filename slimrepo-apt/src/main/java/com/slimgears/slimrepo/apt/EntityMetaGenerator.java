// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.GetterSetterPropertyInfo;
import com.slimgears.slimapt.PropertyFinder;
import com.slimgears.slimapt.TypeUtils;
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

/**
 * Created by ditskovi on 12/24/2015.
 */
public class EntityMetaGenerator extends ClassGenerator<EntityMetaGenerator> {
    private final TypeElement entityTypeElement;

    public EntityMetaGenerator(ProcessingEnvironment processingEnvironment, TypeElement entityTypeElement) {
        super(processingEnvironment);
        this.entityTypeElement = entityTypeElement;
        className(MetaFields.generatedMetaEntityClassName(TypeName.get(entityTypeElement.asType())));
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        PropertyFinder propertyFinder = new PropertyFinder(getElementUtils());
        entityTypeElement.accept(propertyFinder, null);
        TypeName entityTypeName = TypeUtils.getTypeName(entityTypeElement.asType());
        MetaFields metaFields = new MetaFields(entityTypeElement);
        List<GetterSetterPropertyInfo> properties = Stream.of(propertyFinder.getProperties())
                .map(PropertyFinder.PropertyDescriptor::createPropertyInfo)
                .collect(Collectors.toList());

        GetterSetterPropertyInfo keyProperty = MetaFields.getKeyField(getTypeName(), properties);
        properties.remove(keyProperty);
        properties.add(0, keyProperty);
        TypeName keyType = TypeUtils.box(keyProperty.getType());

        for (GetterSetterPropertyInfo prop : properties) {
            builder.addField(metaFields.buildMetaField(entityTypeName, prop));
        }


        builder
                .addAnnotation(AnnotationSpec
                        .builder(Generated.class)
                        .addMember("value", "\"" + entityTypeName.toString() + "\"")
                        .addMember("comments", "\"Entity meta data generated from " + entityTypeName.toString() + "\"")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addField(FieldSpec
                        .builder(ParameterizedTypeName.get(
                                ClassName.get(EntityType.class), keyType, entityTypeName),
                                "EntityMetaType",
                                Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new MetaType()")
                        .build())
                .addType(MetaFields.createMetaType(entityTypeName, keyType, properties));
    }
}
