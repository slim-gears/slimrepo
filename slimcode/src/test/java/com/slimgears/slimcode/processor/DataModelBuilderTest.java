// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

package com.slimgears.slimcode.processor;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by Denis on 28-Mar-15.
 */
@RunWith(JUnit4.class)
public class DataModelBuilderTest {
    @Test
    public void basicProcessing() {
        JavaFileObject sourceFile = JavaFileObjects.forResource("AbstractTestModel.java");
        assert_()
                .about(javaSource())
                .that(sourceFile)
                .processedWith(new DataAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forResource("TestModel.java"));
    }
}
