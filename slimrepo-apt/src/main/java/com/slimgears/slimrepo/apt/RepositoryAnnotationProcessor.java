// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Joiner;
import com.slimgears.slimapt.AnnotationProcessorBase;
import com.slimgears.slimapt.TypeUtils;
import com.slimgears.slimrepo.core.annotations.OrmProvider;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.internal.AbstractRepository;
import com.slimgears.slimrepo.core.internal.AbstractRepositoryService;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.SessionServiceProvider;
import com.squareup.javapoet.*;

import javax.annotation.Generated;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 22-Apr-15
 *
 */
@SupportedAnnotationTypes("com.slimgears.slimrepo.core.annotations.GenerateRepository")
public class RepositoryAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement repositoryTypeElement) throws IOException {
        Elements elementUtils = processingEnv.getElementUtils();
        String packageName = elementUtils.getPackageOf(repositoryTypeElement).getQualifiedName().toString();
        String nameWithoutPackage = repositoryTypeElement.toString().substring(packageName.isEmpty() ? 0 : packageName.length() + 1).replace('.', '_');
        String repositoryImplementationName = "Generated" + nameWithoutPackage;

        new RepositoryGenerator(processingEnv)
                .className(packageName, repositoryImplementationName)
                .addInterfaces(repositoryTypeElement)
                .superClass(AbstractRepository.class)
                .build();

        ClassName repositoryImplClassName = ClassName.get(packageName, repositoryImplementationName);
        String repositoryServiceInterfaceName = nameWithoutPackage + "Service";

        TypeSpec.Builder repositoryServiceInterfaceBuilder = TypeSpec.interfaceBuilder(repositoryServiceInterfaceName)
                .addAnnotation(AnnotationSpec
                        .builder(Generated.class)
                        .addMember("value", "\"" + repositoryTypeElement.asType().toString() + "\"")
                        .addMember("comments", "\"Repository service interface generated from " + repositoryTypeElement.asType().toString() + "\"")
                        .build())
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(RepositoryService.class), TypeName.get(repositoryTypeElement.asType())))
                .addModifiers(Modifier.PUBLIC);


        String repositoryServiceImplementationName = "Generated" + repositoryServiceInterfaceName;

        TypeSpec.Builder repositoryServiceImplementationBuilder = TypeSpec.classBuilder(repositoryServiceImplementationName)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractRepositoryService.class), TypeName.get(repositoryTypeElement.asType())))
                .addAnnotation(AnnotationSpec
                        .builder(Generated.class)
                        .addMember("value", "\"" + repositoryTypeElement.asType().toString() + "\"")
                        .addMember("comments", "\"Repository service implementation generated from " + repositoryTypeElement.asType().toString() + "\"")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(packageName, repositoryServiceInterfaceName))
                .addMethod(MethodSpec.methodBuilder("createRepository")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(SessionServiceProvider.class, "sessionServiceProvider")
                        .returns(TypeName.get(repositoryTypeElement.asType()))
                        .addCode("return new $T(sessionServiceProvider);\n", repositoryImplClassName)
                        .build());

        addRepositoryServiceImplementationConstructors(repositoryServiceImplementationBuilder, repositoryTypeElement, repositoryImplClassName);

        for (ExecutableElement method : getEntityGetterMethods(repositoryTypeElement)) {
            String methodName = method.getSimpleName().toString();
            RepositoryGenerator.EntitySetType entitySetType = RepositoryGenerator.getEntitySetType(processingEnv.getElementUtils(), packageName, method.getReturnType());

            repositoryServiceInterfaceBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(entitySetType.entitySetType)
                    .build());

            repositoryServiceImplementationBuilder.addMethod(MethodSpec.methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addAnnotation(Override.class)
                    .returns(entitySetType.entitySetType)
                    .addCode("return getEntitySet($T.EntityMetaType);\n", entitySetType.entityMeta)
                    .build());
        }

        writeType(packageName, repositoryServiceInterfaceBuilder.build());
        writeType(packageName, repositoryServiceImplementationBuilder.build());

        return true;
    }

    private void addRepositoryServiceImplementationConstructors(TypeSpec.Builder implementationBuilder, TypeElement repositoryTypeElement, TypeName repositoryImplementationType) {
        OrmProvider annotation = repositoryTypeElement.getAnnotation(OrmProvider.class);

        MethodSpec.Builder ctorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(annotation != null ? Modifier.PRIVATE : Modifier.PUBLIC)
                .addParameter(OrmServiceProvider.class, "ormServiceProvider")
                .addCode("super(ormServiceProvider, $T.Model.Instance", repositoryImplementationType);

        if (annotation != null) {
            addRepositoryServiceImplementationConstructors(implementationBuilder, annotation);
            Iterable<? extends TypeName> types = getTypeMappingsAttribute(annotation);
            for (TypeName type : types) {
                ctorBuilder.addCode(",\n    new $T()", type);
            }
        }

        ctorBuilder.addCode(");\n");
        implementationBuilder.addMethod(ctorBuilder.build());
    }

    private Collection<TypeName> getTypeMappingsAttribute(OrmProvider provider) {
        return TypeUtils.getTypesFromAnnotation(provider, OrmProvider::typeMappings);
    }

    private TypeName getValueAttribute(OrmProvider annotation) {
        return TypeUtils.getTypeFromAnnotation(annotation, OrmProvider::value);
    }

    private void addRepositoryServiceImplementationConstructors(final TypeSpec.Builder implementationBuilder, OrmProvider annotation) {
        TypeName ormProviderClass = getValueAttribute(annotation);
        final TypeElement ormServiceProviderTypeElement = processingEnv.getElementUtils().getTypeElement(ormProviderClass.toString());
        Iterable<? extends Element> constructorElements = filter(
                ormServiceProviderTypeElement.getEnclosedElements(),
                input -> input.getKind() == ElementKind.CONSTRUCTOR);

        for (Element element : constructorElements) {
            implementationBuilder.addMethod(createRepositoryServiceImplementationConstructor(ormServiceProviderTypeElement, (ExecutableElement)element));
        }
    }

    private MethodSpec createRepositoryServiceImplementationConstructor(TypeElement ormServiceProviderTypeElement, ExecutableElement ctorElement) {
        MethodSpec.Builder ctorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        Collection<String> parameterNames = new ArrayList<>();
        for (VariableElement parameter : ctorElement.getParameters()) {
            String name = parameter.getSimpleName().toString();
            ctorBuilder.addParameter(TypeName.get(parameter.asType()), name);
            parameterNames.add(name);
        }
        ctorBuilder.addCode("this(new $T(" + Joiner.on(", ").join(parameterNames) + "));\n", TypeName.get(ormServiceProviderTypeElement.asType()));
        return ctorBuilder.build();
    }

    private Iterable<ExecutableElement> getEntityGetterMethods(TypeElement element) {
        List<? extends Element> elements = element.getEnclosedElements();
        return transform(
                filter(elements, input -> (input instanceof ExecutableElement) &&
                        RepositoryGenerator.isEntitySet(processingEnv.getTypeUtils(), ((ExecutableElement) input).getReturnType())),
                input -> (ExecutableElement)input);
    }
}
