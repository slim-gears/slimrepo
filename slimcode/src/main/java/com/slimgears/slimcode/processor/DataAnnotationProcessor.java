// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

package com.slimgears.slimcode.processor;

import java.io.IOException;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 29-Mar-15.
 */
@SupportedAnnotationTypes("com.slimgears.slimcode.annotations.Data")
public class DataAnnotationProcessor extends AnnotationProcessorBase {
    @Override
    protected boolean processType(TypeElement typeElement) throws IOException {
        new DataModelGenerator()
                .className(typeElement.getQualifiedName().toString().replace("Abstract", ""))
                .superClass(typeElement)
                .build(processingEnv.getFiler());
        return true;
    }
}
