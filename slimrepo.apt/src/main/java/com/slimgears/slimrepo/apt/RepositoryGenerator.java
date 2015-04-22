package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.slimgears.slimrepo.apt.base.ClassGenerator;
import com.slimgears.slimrepo.apt.base.ElementVisitorBase;
import com.slimgears.slimrepo.core.annotations.GenerateRepository;
import com.slimgears.slimrepo.core.interfaces.entities.EntitySet;
import com.slimgears.slimrepo.core.internal.DefaultRepositoryModel;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor7;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public class RepositoryGenerator extends ClassGenerator<RepositoryGenerator> {
    protected RepositoryGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    class Visitor extends ElementVisitorBase<Void, Void> {
        private final TypeSpec.Builder builder;
        private final MethodSpec.Builder modelCtorBuilder;
        private final MethodSpec.Builder ctorBuilder;
        private final ProcessingEnvironment environment;

        Visitor(TypeSpec.Builder builder, MethodSpec.Builder modelCtorBuilder, MethodSpec.Builder ctorBuilder) {
            this.builder = builder;
            this.modelCtorBuilder = modelCtorBuilder;
            this.ctorBuilder = ctorBuilder;
            this.environment = getProcessingEnvironment();
        }

        @Override
        public Void visitExecutable(ExecutableElement method, Void param) {
            TypeMirror returnType = method.getReturnType();
            if (!isEntitySet(returnType)) throw new RuntimeException("Only entity set getters are supported");

            final List<TypeMirror> returnTypeArguments = new ArrayList<>();
            returnType.accept(new SimpleTypeVisitor7<Void, Void>() {
                @Override
                public Void visitDeclared(DeclaredType declaredType, Void param) {
                    returnTypeArguments.addAll(declaredType.getTypeArguments());
                    return null;
                }
            }, null);

            TypeName keyType = TypeName.get(returnTypeArguments.get(0));
            TypeName entityType = ClassName.get(getPackageName(), returnTypeArguments.get(1).toString());

            String name = method.getSimpleName().toString();
            String fieldName = (name.startsWith("get") ? toCamelCase("", name.substring(3)) : name) + "EntitySet";
            TypeName fieldType = ParameterizedTypeName.get(ClassName.get(EntitySet.Provider.class), keyType, entityType);

            builder.addField(fieldType, fieldName, Modifier.FINAL, Modifier.PRIVATE);
            ctorBuilder.addCode("this.$L = sessionServiceProvider.getEntitySetProvider($T.EntityMetaType);\n", fieldName, entityType);
            modelCtorBuilder.addCode(", $T.EntityMetaType", entityType);

            builder.addMethod(MethodSpec.methodBuilder(name)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .returns(ParameterizedTypeName.get(ClassName.get(EntitySet.class), keyType, entityType))
                    .addCode("return this.$L.get();\n", fieldName)
                    .build());

            return null;
        }

        private boolean isEntitySet(TypeMirror type) {
            TypeMirror erasure = environment.getTypeUtils().erasure(type);
            String typeStr = erasure.toString();
            String classStr = EntitySet.class.getCanonicalName();
            return typeStr.equals(classStr);
        }
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
                .addModifiers(Modifier.PUBLIC)
                .addType(modelTypeSpec)
                .addMethod(ctorBuilder.build());
    }
}
