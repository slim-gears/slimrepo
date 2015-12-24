// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.slimgears.slimrepo.apt.base.FieldInfo;
import com.slimgears.slimrepo.apt.base.TypeUtils;
import com.slimgears.slimrepo.core.annotations.BlobSemantics;
import com.slimgears.slimrepo.core.annotations.ComparableSemantics;
import com.slimgears.slimrepo.core.annotations.Entity;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.ValueSemantics;
import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.internal.Fields;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by ditskovi on 12/24/2015.
 */
public class MetaFields {
    private static final Map<TypeName, AbstractMetaFieldBuilder> META_FIELD_BUILDER_MAP = new HashMap<>();
    private final Map<TypeName, AbstractMetaFieldBuilder> metaFieldBuilderMap = new HashMap<>(META_FIELD_BUILDER_MAP);
    private final Elements elementUtils;

    public MetaFields(ProcessingEnvironment processingEnvironment, TypeElement type) {
        elementUtils = processingEnvironment.getElementUtils();
        mapFieldTypesFromAnnotation(type, ComparableSemantics.class, TYPES_FROM_COMPARABLE_SEMANTICS, ComparableMetaFieldBuilder.INSTANCE);
        mapFieldTypesFromAnnotation(type, BlobSemantics.class, TYPES_FROM_BLOB_SEMANTICS, BlobMetaFieldBuilder.INSTANCE);
        mapFieldTypesFromAnnotation(type, ValueSemantics.class, TYPES_FROM_VALUE_SEMANTICS, ValueMetaFieldBuilder.INSTANCE);
    }

    public FieldSpec buildMetaField(ClassName entityType, FieldInfo field) {
        return getMetaFieldBuilder(field).build(entityType, field);
    }

    public static String generateEntityTypeName(TypeName superClass) {
        return TypeUtils.simpleName(superClass.toString()).replace("Abstract", "");
    }

    public static TypeName entityTypeFromAbstract(TypeName superClass) {
        String simpleName = generateEntityTypeName(superClass);
        String packageName = TypeUtils.packageName(superClass.toString());
        return ClassName.get(packageName, simpleName);
    }

    private boolean isEnumField(FieldInfo field) {
        TypeElement type = typeElementForField(field);
        return type != null && type.getKind() == ElementKind.ENUM;
    }

    private boolean isRelationalField(FieldInfo field) {
        TypeElement type = typeElementForField(field);
        return (type != null) && (type.getAnnotation(GenerateEntity.class) != null || type.getAnnotation(Entity.class) != null);
    }

    private TypeElement typeElementForField(FieldInfo field) {
        return elementUtils.getTypeElement(field.type.toString());
    }

    public static String getMetaFieldName(String fieldName) {
        return TypeUtils.toCamelCase("", fieldName);
    }

    private AbstractMetaFieldBuilder getMetaFieldBuilder(FieldInfo field) {
        if (isRelationalField(field)) return RelationalMetaFieldBuilder.INSTANCE;
        if (isEnumField(field)) return ComparableMetaFieldBuilder.INSTANCE;
        AbstractMetaFieldBuilder builder = metaFieldBuilderMap.get(field.type);
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

    public static TypeName box(TypeName type) {
        if (type == TypeName.INT) return TypeName.get(Integer.class);
        if (type == TypeName.SHORT) return TypeName.get(Short.class);
        if (type == TypeName.LONG) return TypeName.get(Long.class);
        if (type == TypeName.BOOLEAN) return TypeName.get(Boolean.class);
        if (type == TypeName.DOUBLE) return TypeName.get(Double.class);
        if (type == TypeName.FLOAT) return TypeName.get(Float.class);
        if (type == TypeName.BYTE) return TypeName.get(Byte.class);
        return type;
    }

    private static TypeUtils.AnnotationTypesGetter<ComparableSemantics> TYPES_FROM_COMPARABLE_SEMANTICS = ComparableSemantics::value;
    private static TypeUtils.AnnotationTypesGetter<ValueSemantics> TYPES_FROM_VALUE_SEMANTICS = ValueSemantics::value;
    private static TypeUtils.AnnotationTypesGetter<BlobSemantics> TYPES_FROM_BLOB_SEMANTICS = BlobSemantics::value;

    static abstract class AbstractMetaFieldBuilder {
        public FieldSpec build(ClassName entityType, FieldInfo field) {
            TypeName type = metaFieldType(entityType, field.type);
            FieldSpec.Builder builder = FieldSpec.builder(type, getMetaFieldName(field.name), Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL);
            return initialize(entityType, builder, field).build();
        }

        protected abstract TypeName metaFieldType(ClassName entityType, TypeName fieldType);
        protected abstract FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field);
    }

    static class ValueMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final ValueMetaFieldBuilder INSTANCE = new ValueMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(ValueField.class), entityType, box(fieldType));
        }

        @Override
        protected FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field) {
            TypeName fieldType = box(field.type);
            return builder.initializer(
                    "$T.valueField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    field.name,
                    fieldType,
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, field.getterName,
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, field.setterName,
                    field.isNullable());
        }
    }

    static class ComparableMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final ComparableMetaFieldBuilder INSTANCE = new ComparableMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(ComparableField.class), entityType, box(fieldType));
        }

        @Override
        protected FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field) {
            TypeName fieldType = box(field.type);
            return builder.initializer(
                    "$T.comparableField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    field.name,
                    fieldType,
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, field.getterName,
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, field.setterName,
                    field.isNullable());
        }
    }

    static class StringMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final StringMetaFieldBuilder INSTANCE = new StringMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(StringField.class), entityType);
        }

        @Override
        protected FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field) {
            return builder.initializer(
                    "$T.stringField(" +
                            "\n    $S," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    field.name,
                    ValueGetter.class, entityType, String.class, String.class, entityType, field.getterName,
                    ValueSetter.class, entityType, String.class, entityType, String.class, field.setterName,
                    field.isNullable());
        }
    }

    static class BlobMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final BlobMetaFieldBuilder INSTANCE = new BlobMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(BlobField.class), entityType, fieldType);
        }

        @Override
        protected FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field) {
            TypeName fieldType = box(field.type);
            return builder.initializer(
                    "$T.blobField(" +
                            "\n    $S," +
                            "\n    $T.class," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    field.name,
                    field.type,
                    ValueGetter.class, entityType, fieldType, fieldType, entityType, field.getterName,
                    ValueSetter.class, entityType, fieldType, entityType, fieldType, field.setterName,
                    field.isNullable());
        }
    }

    static class RelationalMetaFieldBuilder extends AbstractMetaFieldBuilder {
        static final RelationalMetaFieldBuilder INSTANCE = new RelationalMetaFieldBuilder();

        @Override
        protected TypeName metaFieldType(ClassName entityType, TypeName fieldType) {
            return ParameterizedTypeName.get(ClassName.get(RelationalField.class), entityType, fieldType);
        }

        @Override
        protected FieldSpec.Builder initialize(ClassName entityType, FieldSpec.Builder builder, FieldInfo field) {
            TypeName relatedEntityType = entityTypeFromAbstract(field.type);
            return builder.initializer(
                    "$T.relationalField(" +
                            "\n    $S," +
                            "\n    $T.EntityMetaType," +
                            "\n    new $T<$T, $T>() { @Override public $T getValue($T entity) { return entity.$L(); } }," +
                            "\n    new $T<$T, $T>() { @Override public void setValue($T entity, $T value) { entity.$L(value); } }," +
                            "\n    $L)",
                    Fields.class,
                    field.name,
                    relatedEntityType,
                    ValueGetter.class, entityType, relatedEntityType, relatedEntityType, entityType, field.getterName,
                    ValueSetter.class, entityType, relatedEntityType, entityType, relatedEntityType, field.setterName,
                    field.isNullable());
        }
    }
}
