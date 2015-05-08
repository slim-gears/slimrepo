// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.slimgears.slimrepo.apt.base.AnnotationProcessorBase;
import com.slimgears.slimrepo.apt.base.TypeUtils;
import com.slimgears.slimrepo.core.annotations.OrmProvider;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
@SupportedAnnotationTypes("com.slimgears.slimrepo.core.annotations.GenerateRepository")
public class RepositoryAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement repositoryTypeElement) throws IOException {
        String repositoryImplementationName = "Generated" + repositoryTypeElement.getSimpleName().toString();
        String packageName = TypeUtils.packageName(repositoryTypeElement.getQualifiedName().toString());

        new RepositoryGenerator(processingEnv)
                .className(packageName, repositoryImplementationName)
                .addInterfaces(repositoryTypeElement)
                .superClass(AbstractRepository.class)
                .build();

        ClassName repositoryImplClassName = ClassName.get(packageName, repositoryImplementationName);
        String repositoryServiceInterfaceName = repositoryTypeElement.getSimpleName().toString() + "Service";

        TypeSpec.Builder repositoryServiceInterfaceBuilder = TypeSpec.interfaceBuilder(repositoryServiceInterfaceName)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(RepositoryService.class), TypeName.get(repositoryTypeElement.asType())))
                .addModifiers(Modifier.PUBLIC);


        String repositoryServiceImplementationName = "Generated" + repositoryServiceInterfaceName;

        TypeSpec.Builder repositoryServiceImplementationBuilder = TypeSpec.classBuilder(repositoryServiceImplementationName)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractRepositoryService.class), TypeName.get(repositoryTypeElement.asType())))
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
        return TypeUtils.getTypesFromAnnotation(provider, new TypeUtils.AnnotationTypesGetter<OrmProvider>() {
            @Override
            public Class[] getTypes(OrmProvider annotation) throws MirroredTypesException {
                return annotation.typeMappings();
            }
        });
    }

    private TypeName getValueAttribute(OrmProvider annotation) {
        return TypeUtils.getTypeFromAnnotation(annotation, new TypeUtils.AnnotationTypeGetter<OrmProvider>() {
            @Override
            public Class getType(OrmProvider annotation) throws MirroredTypeException {
                return annotation.value();
            }
        });
    }

    private void addRepositoryServiceImplementationConstructors(final TypeSpec.Builder implementationBuilder, OrmProvider annotation) {
        TypeName ormProviderClass = getValueAttribute(annotation);
        final TypeElement ormServiceProviderTypeElement = processingEnv.getElementUtils().getTypeElement(ormProviderClass.toString());
        Iterable<? extends Element> constructorElements = filter(
                ormServiceProviderTypeElement.getEnclosedElements(),
                new Predicate<Element>() {
                    @Override
                    public boolean apply(Element input) {
                        return input.getKind() == ElementKind.CONSTRUCTOR;
                    }
                });

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
