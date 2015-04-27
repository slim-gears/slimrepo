package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by ditskovi on 4/27/2015.
 */
public interface RelationalField<TEntity, TRelatedEntity extends Entity<?>> extends Field<TEntity, TRelatedEntity> {
    interface MetaInfo<TEntity, TRelatedEntity extends Entity<?>> extends Field.MetaInfo<TEntity, TRelatedEntity> {
        EntityType<?, TRelatedEntity> relatedEntityType();
    }

    MetaInfo<TEntity, TRelatedEntity> metaInfo();

    Condition<TEntity> is(Condition<TRelatedEntity> condition);
}
