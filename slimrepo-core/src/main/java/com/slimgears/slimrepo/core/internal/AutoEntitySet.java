package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.Repository;
import com.slimgears.slimrepo.core.interfaces.RepositoryService;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;

/**
 * Created by Denis on 02-May-15.
 */
public class AutoEntitySet<TKey, TEntity, TRepository extends Repository> extends DefaultEntitySet<TKey, TEntity> {
    private final RepositoryService<TRepository> repositoryService;
    private final EntityType<TKey, TEntity> entityType;

    public AutoEntitySet(OrmServiceProvider ormServiceProvider, EntityType<TKey, TEntity> entityType, RepositoryModel repositoryModel, RepositoryService<TRepository> repositoryService) {
        super(new AutoSessionEntityServiceProvider<>(ormServiceProvider, repositoryModel, entityType), entityType);
        this.repositoryService = repositoryService;
        this.entityType = entityType;
    }

    @Override
    public void addAll(Iterable<TEntity> entities) throws Exception {
        try (TRepository repository = repositoryService.open()) {
            repository.entities(entityType).addAll(entities);
            repository.saveChanges();
        }
    }

    @Override
    public void removeAll(Iterable<TEntity> entities) throws Exception {
        try (TRepository repository = repositoryService.open()) {
            repository.entities(entityType).removeAll(entities);
            repository.saveChanges();
        }
    }

    @Override
    public void mergeAll(Iterable<TEntity> entities) throws Exception {
        try (TRepository repository = repositoryService.open()) {
            repository.entities(entityType).mergeAll(entities);
            repository.saveChanges();
        }
    }
}
