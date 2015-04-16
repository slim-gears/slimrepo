package com.slimgears.slimorm.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import com.slimgears.slimcode.processor.AnnotationProcessorBase;
import com.slimgears.slimcode.processor.DataModelGenerator;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
@SupportedAnnotationTypes("com.slimgears.slimorm.annotations.EntityTemplate")
public class EntityAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        new DataModelGenerator()
                .className(typeElement.getQualifiedName().toString().replace("Abstract", ""))
                .superClass(typeElement)
                .build(processingEnv.getFiler());
        return true;
    }
}
