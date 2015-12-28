// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.slimgears.slimrepo.apt.base.ClassGenerator;
import com.slimgears.slimrepo.apt.base.ElementVisitorBase;
import com.slimgears.slimrepo.apt.base.GetterSetterPropertyInfo;
import com.slimgears.slimrepo.apt.base.PropertyInfo;
import com.slimgears.slimrepo.apt.base.TypeUtils;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
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
        className(TypeUtils.packageName(entityTypeElement.asType().toString()), TypeUtils.simpleName(entityTypeElement.asType().toString()).concat("Meta"));
    }

    static class PropertyDescriptor {
        String name;
        ExecutableElement getter;
        ExecutableElement setter;

        boolean isValid() {
            return getter != null && setter != null;
        }

        String getName() {
            return TypeUtils.toCamelCase(name);
        }

        GetterSetterPropertyInfo createPropertyInfo() {
            return new GetterSetterPropertyInfo(getName(), getter, setter);
        }
    }

    class PropertyFinder extends ElementVisitorBase<Void, Void> {
        private final Map<String, PropertyDescriptor> properties = new HashMap<>();

        @Override
        public Void visitExecutable(ExecutableElement element, Void param) {
            String name = element.getSimpleName().toString();
            if (name.startsWith("get")) {
                visitGetter(element, name.substring(3));
            } else if (name.startsWith("is")) {
                visitGetter(element, name.substring(2));
            } else if (name.startsWith("set")) {
                visitSetter(element, name.substring(3));
            }
            return null;
        }

        public Collection<PropertyDescriptor> getProperties() {
            return Stream.of(properties.values())
                    .filter(PropertyDescriptor::isValid)
                    .collect(Collectors.toList());
        }

        private void visitGetter(ExecutableElement getter, String name) {
            PropertyDescriptor descriptor = getDescriptor(name);
            descriptor.getter = getter;
        }

        private void visitSetter(ExecutableElement setter, String name) {
            PropertyDescriptor descriptor = getDescriptor(name);
            descriptor.setter = setter;
        }

        private PropertyDescriptor getDescriptor(String name) {
            PropertyDescriptor descriptor = properties.getOrDefault(name, null);
            if (descriptor == null) {
                descriptor = new PropertyDescriptor();
                descriptor.name = name;
                properties.put(name, descriptor);
            }
            return descriptor;
        }
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        PropertyFinder propertyFinder = new PropertyFinder();
        entityTypeElement.accept(propertyFinder, null);
        TypeName entityTypeName = TypeUtils.getTypeName(entityTypeElement.asType());
        MetaFields metaFields = new MetaFields(getProcessingEnvironment(), entityTypeElement);
        List<GetterSetterPropertyInfo> properties = Stream.of(propertyFinder.getProperties())
                .map(PropertyDescriptor::createPropertyInfo)
                .collect(Collectors.toList());

        for (GetterSetterPropertyInfo prop : properties) {
            builder.addField(metaFields.buildMetaField(entityTypeName, prop));
        }

        PropertyInfo keyProperty = MetaFields.getKeyField(getTypeName(), properties);
        TypeName keyType = TypeUtils.box(keyProperty.getType());

        builder
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
