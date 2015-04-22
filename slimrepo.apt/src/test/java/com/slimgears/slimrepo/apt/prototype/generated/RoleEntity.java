// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.apt.prototype.generated;

import com.slimgears.slimrepo.apt.prototype.AbstractRoleEntity;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Fields;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class RoleEntity extends AbstractRoleEntity implements Entity<Integer> {
    public static final EntityType<Integer, RoleEntity> EntityMetaType;
    public static final NumericField<RoleEntity, Integer> RoleId = Fields.numberField("roleId", RoleEntity.class, Integer.class, false);
    public static final StringField<RoleEntity> RoleDescription = Fields.stringField("roleDescription", RoleEntity.class, true);

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

        @Override
        public void setKey(RoleEntity entity, Integer key) {
            entity.setRoleId(key);
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
