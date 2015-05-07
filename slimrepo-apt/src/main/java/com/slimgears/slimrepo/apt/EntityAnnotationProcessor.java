package com.slimgears.slimrepo.apt;
// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.slimgears.slimrepo.apt.base.AnnotationProcessorBase;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
@SupportedAnnotationTypes("com.slimgears.slimrepo.core.annotations.GenerateEntity")
public class EntityAnnotationProcessor extends AnnotationProcessorBase {

    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        new EntityGenerator(processingEnv)
                .superClass(typeElement)
                .build();

        return true;
    }
}
