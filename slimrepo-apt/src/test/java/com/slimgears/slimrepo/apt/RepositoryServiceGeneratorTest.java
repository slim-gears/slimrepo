package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details


import com.google.common.collect.Iterables;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaFileObject;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
@RunWith(JUnit4.class)
public class RepositoryServiceGeneratorTest {
    private static Iterable<JavaFileObject> inputFiles(String... files) {
        return fromResources("input", files);
    }

    private static Iterable<JavaFileObject> expectedFiles(String... files) {
        return fromResources("output", files);
    }

    private static Iterable<JavaFileObject> fromResources(final String path, String[] files) {
        return transform(asList(files), input -> JavaFileObjects.forResource(path + '/' + input));
    }

    private void testAnnotationProcessing(AbstractProcessor processor, Iterable<JavaFileObject> inputs, Iterable<JavaFileObject> expectedOutputs) {
        assert_()
                .about(javaSources())
                .that(inputs)
                .processedWith(processor)
                .compilesWithoutError()
                .and().generatesSources(Iterables.getFirst(expectedOutputs, null), toArray(skip(expectedOutputs, 1), JavaFileObject.class));
    }

    @Test
    public void forAbstractEntities_shouldGenerate_concreteEntities() {
        testAnnotationProcessing(
                new GenerateEntityAnnotationProcessor(),
                inputFiles("AbstractRelatedEntity.java", "AbstractTestEntity.java"),
                expectedFiles("TestEntity.java", "RelatedEntity.java"));
    }

    @Test
    public void forExistingEntities_shouldGenerate_metaModel() {
        testAnnotationProcessing(
                new EntityAnnotationProcessor(),
                inputFiles("ExistingEntity.java"),
                expectedFiles("ExistingEntityMeta.java"));
    }

    @Test
    public void forRepositoryInterface_shouldGenerate_implementationAndRepositoryService() {
        testAnnotationProcessing(
                new RepositoryAnnotationProcessor(),
                inputFiles("TestRepository.java"),
                expectedFiles("GeneratedTestRepository.java", "TestRepositoryService.java", "GeneratedTestRepositoryService.java"));
    }

    @Test
    public void forCustomOrmRepository_shouldGenerate_customRepositoryImplementationAndService() {
        testAnnotationProcessing(
                new RepositoryAnnotationProcessor(),
                inputFiles("CustomOrmRepository.java"),
                expectedFiles("GeneratedCustomOrmRepository.java", "CustomOrmRepositoryService.java", "GeneratedCustomOrmRepositoryService.java"));
    }
}
