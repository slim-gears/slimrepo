// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.base;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 04-Apr-15
 * <File Description>
 */
public abstract class AnnotationProcessorBase extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotationType : annotations) {
            if (!processAnnotation(annotationType, roundEnv)) return false;
        }
        return true;
   }

    protected boolean processAnnotation(TypeElement annotationType, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotationType);
        for (Element element : annotatedElements) {
            if (!(element instanceof TypeElement)) continue;
            try {
                if (!processType((TypeElement)element)) return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    protected abstract boolean processType(TypeElement typeElement) throws IOException;
}
