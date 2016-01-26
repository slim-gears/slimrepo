package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.slimgears.slimapt.ClassGenerator;
import com.slimgears.slimapt.ElementVisitorBase;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimrepo.core.annotations.Entity;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleTypeVisitor7;
import javax.lang.model.util.Types;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public class RepositoryGenerator extends ClassGenerator<RepositoryGenerator> {
    protected RepositoryGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    public static class EntitySetType {
        public final TypeName entityType;
        public final TypeName entitySetType;
        public final TypeName entityMeta;

        public EntitySetType(TypeName entityType, TypeName entityMeta) {
            this.entityType = entityType;
            this.entitySetType = ParameterizedTypeName.get(ClassName.get(EntitySet.class), entityType);
            this.entityMeta = entityMeta;
        }
    }

    class Visitor extends ElementVisitorBase<Void, Void> {
        private final TypeSpec.Builder builder;
        private final MethodSpec.Builder modelCtorBuilder;
        private final MethodSpec.Builder ctorBuilder;

        Visitor(TypeSpec.Builder builder, MethodSpec.Builder modelCtorBuilder, MethodSpec.Builder ctorBuilder) {
            this.builder = builder;
            this.modelCtorBuilder = modelCtorBuilder;
            this.ctorBuilder = ctorBuilder;
        }

        @Override
        public Void visitExecutable(ExecutableElement method, Void param) {
            validateEntitySetGetter(getTypeUtils(), method);
            TypeMirror returnType = method.getReturnType();
            EntitySetType entitySetType = getEntitySetType(getElementUtils(), getPackageName(), returnType);

            String name = method.getSimpleName().toString();
            String fieldName = (name.startsWith("get") ? TypeUtils.toCamelCase("", name.substring(3)) : name) + "EntitySet";
            TypeName fieldType = ParameterizedTypeName.get(ClassName.get(EntitySet.Provider.class), entitySetType.entityType);

            builder.addField(fieldType, fieldName, Modifier.FINAL, Modifier.PRIVATE);
            ctorBuilder.addCode("this.$L = sessionServiceProvider.getEntitySetProvider($T.EntityMetaType);\n", fieldName, entitySetType.entityMeta);
            modelCtorBuilder.addCode(", $T.EntityMetaType", entitySetType.entityMeta);

            builder.addMethod(MethodSpec.methodBuilder(name)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(ParameterizedTypeName.get(ClassName.get(EntitySet.class), entitySetType.entityType))
                    .addCode("return this.$L.get();\n", fieldName)
                    .build());

            return null;
        }
    }

    public static EntitySetType getEntitySetType(Elements elementUtils, String packageName, TypeMirror entitySetType) {
        final List<TypeMirror> returnTypeArguments = new ArrayList<>();
        entitySetType.accept(new SimpleTypeVisitor7<Void, Void>() {
            @Override
            public Void visitDeclared(DeclaredType declaredType, Void param) {
                returnTypeArguments.addAll(declaredType.getTypeArguments());
                return null;
            }
        }, null);

        TypeMirror entityTypeMirror = returnTypeArguments.get(0);
        TypeName entityType = TypeUtils.getTypeName(entityTypeMirror, packageName);
        TypeElement entityTypeElement = elementUtils.getTypeElement(entityType.toString());
        TypeName entityMeta = entityTypeElement != null && entityTypeElement.getAnnotation(Entity.class) != null
                ? MetaFields.generatedMetaEntityClassName(entityType)
                : entityType;

        return new EntitySetType(entityType, entityMeta);
    }

    public static void validateEntitySetGetter(Types typeUtils, ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        if (!isEntitySet(typeUtils, returnType)) throw new RuntimeException("Only entity set getters are supported");
        if (method.getParameters().size() > 0) throw new RuntimeException("Entity set getter should not take any parameters");
    }

    public static boolean isEntitySet(Types typeUtils, TypeMirror type) {
        TypeMirror erasure = typeUtils.erasure(type);
        String typeStr = erasure.toString();
        String classStr = EntitySet.class.getCanonicalName();
        return typeStr.equals(classStr);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {
        MethodSpec.Builder ctorBuilder = MethodSpec.constructorBuilder()
                .addParameter(SessionServiceProvider.class, "sessionServiceProvider")
                .addCode("super(sessionServiceProvider);\n");

        MethodSpec.Builder modelCtorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addCode("super(Name, Version");

        TypeElement repositoryInterface = interfaces[0];
        new Visitor(builder, modelCtorBuilder, ctorBuilder).visit(repositoryInterface);

        GenerateRepository annotation = repositoryInterface.getAnnotation(GenerateRepository.class);

        modelCtorBuilder.addCode(");\n");
        TypeSpec modelTypeSpec = TypeSpec.classBuilder("Model")
                .superclass(DefaultRepositoryModel.class)
                .addModifiers(Modifier.STATIC)
                .addField(FieldSpec
                        .builder(ClassName.get(getPackageName(), getClassName(), "Model"), "Instance", Modifier.STATIC, Modifier.PUBLIC, Modifier.FINAL)
                        .initializer("new Model()")
                        .build())
                .addField(FieldSpec
                        .builder(int.class, "Version", Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("$L", annotation.version())
                        .build())
                .addField(FieldSpec
                        .builder(String.class, "Name", Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("$S", annotation.name())
                        .build())
                .addMethod(modelCtorBuilder.build())
                .build();

        builder
                .addAnnotation(AnnotationSpec
                        .builder(Generated.class)
                        .addMember("value", "\"" + repositoryInterface.toString() + "\"")
                        .addMember("comments", "\"Repository generated from " + repositoryInterface.toString() + "\"")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addType(modelTypeSpec)
                .addMethod(ctorBuilder.build());
    }
}
