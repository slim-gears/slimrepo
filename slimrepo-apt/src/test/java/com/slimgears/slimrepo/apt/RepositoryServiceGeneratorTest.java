package com.slimgears.slimrepo.apt;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details


import com.slimgears.slimapt.AnnotationProcessingTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Denis on 03-Apr-15
 *
 */
@RunWith(JUnit4.class)
public class RepositoryServiceGeneratorTest extends AnnotationProcessingTestBase {
    @Test
    public void forAbstractEntities_shouldGenerate_concreteEntities() {
        testAnnotationProcessing(
                processedWith(new GenerateEntityAnnotationProcessor(), new EntityAnnotationProcessor()),
                inputFiles("ExistingEntity.java", "AbstractRelatedEntity.java", "AbstractTestEntity.java"),
                expectedFiles("TestEntity.java", "RelatedEntity.java", "ExistingEntityMeta.java"));
    }

    @Test
    public void forExistingEntities_shouldGenerate_metaModel() {
        testAnnotationProcessing(
                processedWith(new GenerateEntityAnnotationProcessor(), new EntityAnnotationProcessor()),
                inputFiles("ExistingEntity.java", "AbstractRelatedEntity.java"),
                expectedFiles("ExistingEntityMeta.java"));
    }

    @Test
    public void forRepositoryInterface_shouldGenerate_implementationAndRepositoryService() {
        testAnnotationProcessing(
                processedWith(new GenerateEntityAnnotationProcessor(), new EntityAnnotationProcessor(), new RepositoryAnnotationProcessor()),
                inputFiles("TestRepository.java", "ExistingEntity.java", "AbstractRelatedEntity.java"),
                expectedFiles("GeneratedTestRepository.java", "TestRepositoryService.java", "GeneratedTestRepositoryService.java"));
    }

    @Test
    public void forCustomOrmRepository_shouldGenerate_customRepositoryImplementationAndService() {
        testAnnotationProcessing(
                processedWith(new RepositoryAnnotationProcessor()),
                inputFiles("CustomOrmRepository.java"),
                expectedFiles("GeneratedCustomOrmRepository.java", "CustomOrmRepositoryService.java", "GeneratedCustomOrmRepositoryService.java"));
    }

    @Test
    public void forInnerRepository_shouldGenerate_repositoryImplementationAndService() {
        testAnnotationProcessing(
                processedWith(new GenerateEntityAnnotationProcessor(), new EntityAnnotationProcessor(), new RepositoryAnnotationProcessor()),
                inputFiles("RepositoryContainer.java"),
                expectedFiles("GeneratedRepositoryContainer_InnerRepository.java", "RepositoryContainer_InnerRepositoryService.java", "GeneratedRepositoryContainer_InnerRepositoryService.java"));
    }
}
