// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
public class AnnotationProcessor implements Processor {
    private final Processor[] processors = {
            new EntityAnnotationProcessor(),
            new RepositoryAnnotationProcessor()
    };

    @Override
    public Set<String> getSupportedOptions() {
        Set<String> options = new HashSet<>();
        for (Processor p : processors) {
            options.addAll(p.getSupportedOptions());
        }
        return options;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new HashSet<>();
        for (Processor p : processors) {
            annotationTypes.addAll(p.getSupportedAnnotationTypes());
        }
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        SourceVersion minVersion = SourceVersion.RELEASE_7;
        for (Processor p : processors) {
            if (p.getSupportedSourceVersion().ordinal() < minVersion.ordinal()) minVersion = p.getSupportedSourceVersion();
        }
        return minVersion;
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        for (Processor p : processors) {
            p.init(processingEnv);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, TypeElement> annotationMap = new HashMap<>();
        for (TypeElement type : annotations) {
            annotationMap.put(type.getQualifiedName().toString(), type);
        }

        for (Processor p : processors) {
            Set<TypeElement> tmpAnnotations = new HashSet<>();
            Set<String> supportedAnnotations = p.getSupportedAnnotationTypes();
            for (String annotation : supportedAnnotations) {
                if (annotationMap.containsKey(annotation)) {
                    tmpAnnotations.add(annotationMap.get(annotation));
                }
            }
            if (!p.process(tmpAnnotations, roundEnv)) return false;
        }

        return true;
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotation, ExecutableElement member, String userText) {
        return Collections.emptyList();
    }
}
