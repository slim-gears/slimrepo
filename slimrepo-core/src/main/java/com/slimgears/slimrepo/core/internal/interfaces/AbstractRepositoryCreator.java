package com.slimgears.slimrepo.core.internal.interfaces;

public abstract class AbstractRepositoryCreator implements RepositoryCreator {
    @Override
    public void upgradeOrCreate(RepositoryModel model) throws Exception {
        if (!repositoryExists(model)) {
            createRepository(model);
        } else if (repositoryRequiresUpgrade(model)) {
            upgradeRepository(model);
        }
    }

    protected abstract boolean repositoryExists(RepositoryModel model);
    protected abstract boolean repositoryRequiresUpgrade(RepositoryModel model);
}
