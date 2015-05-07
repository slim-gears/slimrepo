// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

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

        TypeSpec.Builder repositoryServiceInterfaceBuilder = TypeSpec.interfaceBuilder(repositoryServiceInterfaceName)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(RepositoryService.class), TypeName.get(typeElement.asType())))
                .addModifiers(Modifier.PUBLIC);


        String repositoryServiceImplementationName = "Generated" + repositoryServiceInterfaceName;

        TypeSpec.Builder repositoryServiceImplementationBuilder = TypeSpec.classBuilder(repositoryServiceImplementationName)
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
                        .build());

        for (ExecutableElement method : getEntityGetterMethods(typeElement)) {
            String methodName = method.getSimpleName().toString();
            RepositoryGenerator.EntitySetType entitySetType = RepositoryGenerator.getEntitySetType(packageName, method.getReturnType());

            repositoryServiceInterfaceBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(entitySetType.entitySetType)
                    .build());

            repositoryServiceImplementationBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .returns(entitySetType.entitySetType)
                    .addCode("return getEntitySet($T.EntityMetaType);\n", entitySetType.entityType)
                    .build());
        }

        writeType(packageName, repositoryServiceInterfaceBuilder.build());
        writeType(packageName, repositoryServiceImplementationBuilder.build());

        return true;
    }

    private void writeType(String packageName, TypeSpec type) throws IOException {
        JavaFile javaFile = JavaFile
                .builder(packageName, type)
                .indent("    ")
                .build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    private Iterable<ExecutableElement> getEntityGetterMethods(TypeElement element) {
        List<? extends Element> elements = element.getEnclosedElements();
        Collection<ExecutableElement> methods = transform(
                filter(elements, new Predicate<Element>() {
                    @Override
                    public boolean apply(Element input) {
                        return (input instanceof ExecutableElement) &&
                                RepositoryGenerator.isEntitySet(processingEnv.getTypeUtils(), ((ExecutableElement) input).getReturnType());
                    }
                }),
                new Function<Element, ExecutableElement>() {
                    @Override
                    public ExecutableElement apply(Element input) {
                        return (ExecutableElement)input;
                    }
                });
        return methods;
    }
}
