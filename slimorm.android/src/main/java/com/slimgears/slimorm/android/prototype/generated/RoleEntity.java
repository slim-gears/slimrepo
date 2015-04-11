// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.generated;

import com.slimgears.slimorm.android.prototype.AbstractRoleEntity;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.interfaces.FieldValueMap;
import com.slimgears.slimorm.interfaces.fields.Fields;
import com.slimgears.slimorm.interfaces.fields.NumberField;
import com.slimgears.slimorm.interfaces.fields.StringField;
import com.slimgears.slimorm.internal.AbstractEntityType;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class RoleEntity extends AbstractRoleEntity implements Entity<Integer> {
    public static final EntityType<Integer, RoleEntity> EntityMetaType;
    public static final NumberField<RoleEntity, Integer> RoleId = Fields.numberField(RoleEntity.class, "roleId", Integer.class);
    public static final StringField<RoleEntity> RoleDescription = Fields.stringField(RoleEntity.class, "roleDescription");

    static {
        EntityMetaType = new MetaType().addFields(RoleDescription).addRelatedEntities();
    }

    static class MetaType extends AbstractEntityType<Integer, RoleEntity> {
        protected MetaType() {
            super("RoleEntity", RoleEntity.class, RoleId);
        }

        @Override
        public RoleEntity newInstance() {
            return new RoleEntity();
        }

        @Override
        public RoleEntity newInstance(FieldValueLookup<RoleEntity> lookup) {
            return newInstance()
                    .setRoleId(lookup.getValue(RoleId))
                    .setRoleDescription(lookup.getValue(RoleDescription));
        }

        @Override
        public void entityToMap(RoleEntity entity, FieldValueMap<RoleEntity> map) {
            map
                    .putValue(RoleId, entity.getRoleId())
                    .putValue(RoleDescription, entity.getRoleDescription());
        }
    }

    @Override
    public Integer getEntityId() {
        return getRoleId();
    }

    public static class Builder {
        private RoleEntity model = new RoleEntity();

        public Builder roleId(int id) {
            model.setRoleId(id);
            return this;
        }

        public Builder roleDescription(String desc) {
            model.setRoleDescription(desc);
            return this;
        }

        public RoleEntity build() {
            return model;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public int getRoleId() {
        return roleId;
    }

    public RoleEntity setRoleId(int id) {
        this.roleId = id;
        return this;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public RoleEntity setRoleDescription(String desc) {
        this.roleDescription = desc;
        return this;
    }
}
