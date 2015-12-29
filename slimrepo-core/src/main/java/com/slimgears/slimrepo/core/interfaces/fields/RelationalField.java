package com.slimgears.slimrepo.core.interfaces.fields;

import com.slimgears.slimrepo.core.interfaces.conditions.Condition;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;

/**
 * Created by ditskovi on 4/27/2015.
 */
public interface RelationalField<TEntity, TRelatedEntity> extends Field<TEntity, TRelatedEntity> {
    interface MetaInfo<TRelatedEntity> extends Field.MetaInfo<TRelatedEntity> {
        EntityType<?, ?> getRelatedEntityType();
    }

    MetaInfo<TRelatedEntity> metaInfo();

    Condition<TEntity> is(Condition<TRelatedEntity> condition);
}
