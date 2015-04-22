// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.slimgears.slimrepo.apt.base.AnnotationProcessorBase;
import com.slimgears.slimrepo.apt.base.ClassGenerator;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
@SupportedAnnotationTypes("com.slimgears.slimrepo.core.annotations.GenerateRepository")
public class RepositoryAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        String repositoryImplementationName = "Generated" + typeElement.getSimpleName().toString();
        String packageName = ClassGenerator.packageName(typeElement.getQualifiedName().toString());

        new RepositoryGenerator(processingEnv)
                .className(packageName, repositoryImplementationName)
                .addInterfaces(typeElement)
                .superClass(AbstractRepository.class)
                .build();

        ClassName repositoryImplName = ClassName.get(packageName, repositoryImplementationName);
        String repositoryServiceInterfaceName = typeElement.getSimpleName().toString() + "Service";

        TypeSpec repositoryServiceInterfaceType = TypeSpec.interfaceBuilder(repositoryServiceInterfaceName)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(RepositoryService.class), TypeName.get(typeElement.asType())))
                .addModifiers(Modifier.PUBLIC)
                .build();


        String repositoryServiceImplementationName = "Generated" + repositoryServiceInterfaceName;

        TypeSpec repositoryServiceImplementationType = TypeSpec.classBuilder(repositoryServiceImplementationName)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractRepositoryService.class), TypeName.get(typeElement.asType())))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(packageName, repositoryServiceInterfaceName))
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(OrmServiceProvider.class, "ormServiceProvider")
                        .addCode("super(ormServiceProvider, $T.Model.Instance);\n", repositoryImplName)
                        .build())
                .addMethod(MethodSpec.methodBuilder("createRepository")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(SessionServiceProvider.class, "sessionServiceProvider")
                        .returns(TypeName.get(typeElement.asType()))
                        .addCode("return new $T(sessionServiceProvider);\n", repositoryImplName)
                        .build())
                .build();

        writeType(packageName, repositoryServiceInterfaceType);
        writeType(packageName, repositoryServiceImplementationType);

        return true;
    }

    private void writeType(String packageName, TypeSpec type) throws IOException {
        JavaFile javaFile = JavaFile
                .builder(packageName, type)
                .indent("    ")
                .build();
        javaFile.writeTo(processingEnv.getFiler());
    }
}
