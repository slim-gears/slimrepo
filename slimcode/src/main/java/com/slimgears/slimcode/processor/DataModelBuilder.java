// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

package com.slimgears.slimcode.processor;

import com.slimgears.slimcode.annotations.Data;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 29-Mar-15.
 */
@SupportedAnnotationTypes("com.slimgears.slimcode.annotations.Data")
public class DataModelBuilder extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Data.class);
        for (Element element : annotatedElements) {
            if (!(element instanceof TypeElement)) continue;
            try {
                generateDataModel((TypeElement)element);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void generateDataModel(TypeElement element) throws IOException {
        Set<Modifier> modifiers = element.getModifiers();
        String className = element.getQualifiedName().toString();
        String simpleClassName = element.getSimpleName().toString();
        String packageName = className.substring(0, className.length() - (simpleClassName.length()));
        String modelSimpleName = simpleClassName.replace("Abstract", "");
        if (packageName.endsWith(".")) packageName = packageName.substring(0, packageName.length() - 1);

        TypeSpec.Builder model = TypeSpec.classBuilder(modelSimpleName)
                .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]))
                .superclass(TypeName.get(element.asType()));

        TypeSpec.Builder builder = TypeSpec.classBuilder("Builder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        TypeName modelType = ClassName.get(packageName, modelSimpleName);
        TypeName builderType = ClassName.get(packageName, modelSimpleName, "Builder");
        builder.addField(FieldSpec
                .builder(modelType, "model", Modifier.PRIVATE)
                .initializer("new $T()", modelType)
                .build());

        builder.addMethod(MethodSpec
                .methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(modelType)
                .addCode("return model;\n")
                .build());

        MethodSpec.Builder modelCtorBuilder = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        model.addMethod(MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build());

        model.addMethod(MethodSpec
                .methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(builderType)
                .addCode("return new Builder();\n")
                .build());


        for (Element child : element.getEnclosedElements()) {
            if (child.getKind() == ElementKind.FIELD) {
                String name = child.getSimpleName().toString();

                TypeName type = TypeName.get(child.asType());

                model.addMethod(createModelSetter(name, type, modelType));
                model.addMethod(createModelGetter(name, type));
                builder.addMethod(createBuilderSetter(name, type, builderType));
                modelCtorBuilder
                        .addParameter(type, name)
                        .addCode("$L = $L;\n", name, name);
            }
        }

        model.addType(builder.build());
        model.addMethod(modelCtorBuilder.build());

        TypeSpec typeSpec = model.build();
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(processingEnv.getFiler());
    }

    MethodSpec createModelGetter(String fieldName, TypeName fieldType) {
        return MethodSpec
                .methodBuilder(toCamelCase("get", fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addCode("return this.$L;\n", fieldName)
                .build();
    }

    MethodSpec createModelSetter(String fieldName, TypeName fieldType, TypeName modelType) {
        return MethodSpec
                .methodBuilder(toCamelCase("set", fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(modelType)
                .addCode("this.$L = $L;\n", fieldName, fieldName)
                .addCode("return this;\n")
                .build();
    }

    MethodSpec createBuilderSetter(String fieldName, TypeName fieldType, TypeName builderType) {
        return MethodSpec
                .methodBuilder(fieldName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(builderType)
                .addCode("model.$L($L);\n", toCamelCase("set", fieldName), fieldName)
                .addCode("return this;\n")
                .build();
    }

    String toCamelCase(String begin, String... parts) {
        String name = begin;
        for (String part : parts) {
            name += Character.toUpperCase(part.charAt(0)) + part.substring(1);
        }
        return name;
    }
}
