package com.slimgears.slimrepo.apt;
// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.slimgears.slimapt.AnnotationProcessorBase;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.io.IOException;

/**
 * Created by Denis on 02-Apr-15
 *
 */
@SupportedAnnotationTypes("com.slimgears.slimrepo.core.annotations.GenerateEntity")
public class GenerateEntityAnnotationProcessor extends AnnotationProcessorBase {

    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        new EntityGenerator(processingEnv)
                .superClass(typeElement)
                .build();

        return true;
    }
}
