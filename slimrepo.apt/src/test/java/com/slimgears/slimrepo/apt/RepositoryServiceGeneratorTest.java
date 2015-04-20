package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details


import com.google.testing.compile.JavaFileObjects;
import com.slimgears.slimrepo.apt.EntityAnnotationProcessor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class RepositoryServiceGeneratorTest {
    private void testAnnotationProcessing(AbstractProcessor processor, String inputResource, String expectedOutputResource) {
        JavaFileObject sourceFile = JavaFileObjects.forResource("input/" + inputResource);
        assert_()
                .about(javaSource())
                .that(sourceFile)
                .processedWith(processor)
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("output/" + expectedOutputResource));

    }

    @Test
    public void entityGenerationTest() {
        testAnnotationProcessing(new EntityAnnotationProcessor(), "AbstractTestEntity.java", "TestEntity.java");
    }
}
