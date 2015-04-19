package com.slimgears.slimorm.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by Denis on 02-Apr-15
 * <File Description>
 */
public class QueryableGenerator extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
