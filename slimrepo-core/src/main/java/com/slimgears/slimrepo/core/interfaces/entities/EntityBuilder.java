package com.slimgears.slimrepo.core.interfaces.entities;

/**
 * Created by Denis on 02-May-15.
 */
public interface EntityBuilder<TEntity extends Entity<?>> {
    TEntity build();
}
