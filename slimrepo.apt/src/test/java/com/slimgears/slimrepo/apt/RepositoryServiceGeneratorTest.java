package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details


import com.google.common.base.Function;
import com.google.testing.compile.JavaFileObjects;
import com.slimgears.slimrepo.apt.EntityAnnotationProcessor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaFileObject;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class RepositoryServiceGeneratorTest {
    private void testAnnotationProcessing(AbstractProcessor processor, String inputResource, String expectedOutputResource, String... otherExpectedOutputResources) {
        JavaFileObject sourceFile = JavaFileObjects.forResource("input/" + inputResource);
        assert_()
                .about(javaSource())
                .that(sourceFile)
                .processedWith(processor)
                .compilesWithoutError()
                .and().generatesSources(toJavaFileObject(expectedOutputResource), toJavaFileObjects(otherExpectedOutputResources));
    }

    private JavaFileObject toJavaFileObject(String expectedOutputResource) {
        return JavaFileObjects.forResource("output/" + expectedOutputResource);
    }

    private JavaFileObject[] toJavaFileObjects(String... expectedOutputResource) {
        return transform(Arrays.asList(expectedOutputResource), new Function<String, JavaFileObject>() {
                        @Override
                        public JavaFileObject apply(String input) {
                            return toJavaFileObject(input);
                        }
                    })
                .toArray(new JavaFileObject[expectedOutputResource.length]);
    }

    @Test
    public void entityGenerationTest() {
        testAnnotationProcessing(new EntityAnnotationProcessor(), "AbstractTestEntity.java", "TestEntity.java");
    }

    @Test
    public void repositoryGenerationTest() {
        testAnnotationProcessing(new RepositoryAnnotationProcessor(), "TestRepository.java", "GeneratedTestRepository.java", "TestRepositoryService.java", "GeneratedTestRepositoryService.java");
    }
}
