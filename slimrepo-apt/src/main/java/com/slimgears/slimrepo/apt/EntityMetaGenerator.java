// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt;

import com.slimgears.slimrepo.apt.base.ClassGenerator;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by ditskovi on 12/24/2015.
 */
public class EntityMetaGenerator extends ClassGenerator<EntityMetaGenerator> {
    protected EntityMetaGenerator(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    @Override
    protected void build(TypeSpec.Builder builder, TypeElement type, TypeElement... interfaces) {

    }
}
