// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.slimgears.slimrepo.apt.base.PropertyInfo;
import com.slimgears.slimrepo.apt.base.TypeUtils;
import com.slimgears.slimrepo.core.annotations.BlobSemantics;
import com.slimgears.slimrepo.core.annotations.ComparableSemantics;
import com.slimgears.slimrepo.core.annotations.Entity;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.annotations.ValueSemantics;
import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by ditskovi on 12/24/2015.
 */
@SuppressWarnings("StaticPseudoFunctionalStyleMethod")
public class MetaFields {
    private static final Map<TypeName, AbstractMetaFieldBuilder> META_FIELD_BUILDER_MAP = new HashMap<>();
    private final Map<TypeName, AbstractMetaFieldBuilder> metaFieldBuilderMap = new HashMap<>(META_FIELD_BUILDER_MAP);

    public MetaFields(TypeElement type) {
        mapFieldTypesFromAnnotation(type, ComparableSemantics.class, TYPES_FROM_COMPARABLE_SEMANTICS, ComparableMetaFieldBuilder.INSTANCE);
        mapFieldTypesFromAnnotation(type, BlobSemantics.class, TYPES_FROM_BLOB_SEMANTICS, BlobMetaFieldBuilder.INSTANCE);
        mapFieldTypesFromAnnotation(type, ValueSemantics.class, TYPES_FROM_VALUE_SEMANTICS, ValueMetaFieldBuilder.INSTANCE);
    }

    public FieldSpec buildMetaField(TypeName entityType, PropertyInfo prop) {
        return getMetaFieldBuilder(prop).build(entityType, prop);
    }

    public static <P extends PropertyInfo> TypeSpec createMetaType(TypeName entityType, TypeName keyType, Iterable<P> props) {
        return TypeSpec.classBuilder("MetaType")
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractEntityType.class), keyType, entityType))
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addMethod(MethodSpec.constructorBuilder()
                        .addCode("super($T.class, ", entityType)
                        .addCode(Joiner
                                .on(", ")
                                .join(Iterables.transform(props, prop -> getMetaFieldName(prop.getName()))))
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


    public static String generatedEntityTypeName(TypeName superClass) {
        return TypeUtils.simpleName(superClass.toString()).replace("Abstract", "");
    }

    public static ClassName generatedMetaEntityClassName(TypeName entityClass) {
        return ClassName.get(TypeUtils.packageName(entityClass.toString()), TypeUtils.simpleName(entityClass.toString()).concat("Meta"));
    }

    public static TypeName entityTypeFromAbstract(TypeName superClass) {
        String simpleName = generatedEntityTypeName(superClass);
        String packageName = TypeUtils.packageName(superClass.toString());
        return ClassName.get(packageName, simpleName);
    }

    public static <P extends PropertyInfo> P getKeyField(TypeName entityTypeName, Iterable<? extends P> fields) {
        P field = findAnnotatedField(fields, Key.class);
        return field != null ? field : findFieldByName(fields, "id", getEntityIdFieldName(entityTypeName));
    }

    private static String getEntityIdFieldName(TypeName entityTypeName) {
        return TypeUtils.toCamelCase(entityTypeName.toString().replace("Entity", ""), "Id");
    }

    private static <P extends PropertyInfo> P findFieldByName(Iterable<? extends P> fields, String... names) {
        final Set<String> nameSet = new HashSet<>(Arrays.asList(names));
        return Iterables.find(fields, field -> nameSet.contains(field.getName()));
    }

    private static <P extends PropertyInfo> P findAnnotatedField(Iterable<? extends P> fields, final Class annotationClass) {
        return (P)Iterables.find(fields, input -> input.getAnnotation(annotationClass) != null, null);
    }

    private static boolean isEnumField(PropertyInfo prop) {
        TypeElement type = prop.getTypeElement();
        return type != null && type.getKind() == ElementKind.ENUM;
    }

    private static boolean isRelationalField(PropertyInfo prop, Class<? extends Annotation> annotationClass) {
        TypeElement type = prop.getTypeElement();
        return (type != null) && (type.getAnnotation(annotationClass) != null);
    }

    public static String getMetaFieldName(String fieldName) {
        return TypeUtils.toCamelCase("", fieldName);
    }

    private AbstractMetaFieldBuilder getMetaFieldBuilder(PropertyInfo prop) {
        if (isRelationalField(prop, GenerateEntity.class)) return RelationalGeneratedEntityMetaFieldBuilder.INSTANCE;
        if (isRelationalField(prop, Entity.class)) return RelationalEntityMetaFieldBuilder.INSTANCE;
        if (isEnumField(prop)) return ComparableMetaFieldBuilder.INSTANCE;
        AbstractMetaFieldBuilder builder = metaFieldBuilderMap.get(prop.getType());
        return builder != null ? builder : BlobMetaFieldBuilder.INSTANCE;
    }

    private <TAnnotation extends Annotation> void mapFieldTypesFromAnnotation(TypeElement typeElement,
                                                                              Class<TAnnotation> annotationType,
                                                                              TypeUtils.AnnotationTypesGetter<TAnnotation> getter,
                                                                              AbstractMetaFieldBuilder builder) {
        TAnnotation annotation = typeElement.getAnnotation(annotationType);
        if (annotation == null) return;

        for (TypeName type : TypeUtils.getTypesFromAnnotation(annotation, getter)) {
            metaFieldBuilderMap.put(type, builder);
        }
    }

    static {
        META_FIELD_BUILDER_MAP.put(TypeName.INT, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.SHORT, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.LONG, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.BYTE, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.DOUBLE, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.FLOAT, ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.get(Date.class), ComparableMetaFieldBuilder.INSTANCE);
        META_FIELD_BUILDER_MAP.put(TypeName.get(String.class), StringMetaFieldBuilder.INSTANCE);
    }

    private static TypeUtils.AnnotationTypesGetter<ComparableSemantics> TYPES_FROM_COMPARABLE_SEMANTICS = ComparableSemantics::value;
    private static TypeUtils.AnnotationTypesGetter<ValueSemantics> TYPES_FROM_VALUE_SEMANTICS = ValueSemantics::value;
    private static TypeUtils.AnnotationTypesGetter<BlobSemantics> TYPES_FROM_BLOB_SEMANTICS = BlobSemantics::value;

    static abstract class AbstractMetaFieldBuilder {
        public FieldSpec build(TypeName entityType, PropertyInfo prop) {
            TypeName type = metaFieldType(entityType, prop.getType());
            FieldSpec.Builder builder = FieldSpec.builder(type, getMetaFieldName(prop.getName()), Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
            return initialize(entityType, builder, prop).build();
        }

        protected abstract TypeName metaFieldType(TypeName entityType, TypeName fieldType);
        protected abstract FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop);
    }

    static class ValueMetaFieldBuilder extends AbstractMetaFieldBuilder {
        public static final ValueMetaFieldBuilder INSTANCE = new ValueMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(ValueField.class), entityType, TypeUtils.box(fieldType));
        }

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            TypeName fieldType = TypeUtils.box(prop.getType());
            return builder.initializer(
                    "$T.valueField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    fieldType,
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, prop.getSetterName(),
                    prop.isNullable());
        }
    }

    static class ComparableMetaFieldBuilder extends AbstractMetaFieldBuilder {
        public static final ComparableMetaFieldBuilder INSTANCE = new ComparableMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(ComparableField.class), entityType, TypeUtils.box(fieldType));
        }

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            TypeName fieldType = TypeUtils.box(prop.getType());
            return builder.initializer(
                    "$T.comparableField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    fieldType,
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, prop.getSetterName(),
                    prop.isNullable());
        }
    }

    static class StringMetaFieldBuilder extends AbstractMetaFieldBuilder {
        public static final StringMetaFieldBuilder INSTANCE = new StringMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(StringField.class), entityType);
        }

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            return builder.initializer(
                    "$T.stringField(" +
                            "\n    $S," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    ValueGetter.class, entityType, String.class, String.class, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, String.class, entityType, String.class, prop.getSetterName(),
                    prop.isNullable());
        }
    }

    static class BlobMetaFieldBuilder extends AbstractMetaFieldBuilder {
        public static final BlobMetaFieldBuilder INSTANCE = new BlobMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(BlobField.class), entityType, fieldType);
        }

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            TypeName fieldType = TypeUtils.box(prop.getType());
            return builder.initializer(
                    "$T.blobField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    prop.getType(),
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, prop.getSetterName(),
                    prop.isNullable());
        }
    }

    static abstract class AbstractRelationalMetaFieldBuilder extends AbstractMetaFieldBuilder {
        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(RelationalField.class), entityType, fieldType);
        }
    }

    static class RelationalGeneratedEntityMetaFieldBuilder extends AbstractRelationalMetaFieldBuilder {
        public static final RelationalGeneratedEntityMetaFieldBuilder INSTANCE = new RelationalGeneratedEntityMetaFieldBuilder();

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            TypeName relatedEntityType = entityTypeFromAbstract(prop.getType());
            return builder.initializer(
                    "$T.relationalField(" +
                            "\n    $S," +
                            "\n    $T.EntityMetaType," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    relatedEntityType,
                    ValueGetter.class, entityType, relatedEntityType, relatedEntityType, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, relatedEntityType, entityType, relatedEntityType, prop.getSetterName(),
                    prop.isNullable());
        }
    }

    static class RelationalEntityMetaFieldBuilder extends AbstractRelationalMetaFieldBuilder {
        public static final RelationalEntityMetaFieldBuilder INSTANCE = new RelationalEntityMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(TypeName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(RelationalField.class), entityType, fieldType);
        }

        @Override
        protected FieldSpec.Builder initialize(TypeName entityType, FieldSpec.Builder builder, PropertyInfo prop) {
            TypeName relatedEntityType = entityTypeFromAbstract(prop.getType());
            ClassName metaEntityClassName = generatedMetaEntityClassName(relatedEntityType);
            return builder.initializer(
                    "$T.relationalField(" +
                            "\n    $S," +
                            "\n    $T.EntityMetaType," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    prop.getName(),
                    metaEntityClassName,
                    ValueGetter.class, entityType, relatedEntityType, relatedEntityType, entityType, prop.getGetterName(),
                    ValueSetter.class, entityType, relatedEntityType, entityType, relatedEntityType, prop.getSetterName(),
                    prop.isNullable());
        }
    }
}
