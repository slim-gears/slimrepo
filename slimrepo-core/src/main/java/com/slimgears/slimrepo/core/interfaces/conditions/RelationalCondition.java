// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.interfaces.conditions;

import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;

/**
 * Created by ditskovi on 4/28/2015.
 */
public interface RelationalCondition<TEntity, TRelatedEntity> extends FieldCondition<TEntity, TRelatedEntity> {
    Condition<TRelatedEntity> getCondition();
    RelationalField<TEntity, TRelatedEntity> getField();
}
