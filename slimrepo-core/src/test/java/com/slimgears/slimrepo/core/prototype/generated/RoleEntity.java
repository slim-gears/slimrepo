// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.prototype.AbstractRoleEntity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.internal.Fields;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class RoleEntity extends AbstractRoleEntity {
    public static final ComparableField<RoleEntity, Integer> RoleId = Fields.comparableField("roleId", Integer.class, RoleEntity::getRoleId, RoleEntity::setRoleId, false);
    public static final StringField<RoleEntity> RoleDescription = Fields.stringField("roleDescription", RoleEntity::getRoleDescription, RoleEntity::setRoleDescription, true);
    public static final EntityType<Integer, RoleEntity> EntityMetaType = new MetaType();

    static class MetaType extends AbstractEntityType<Integer, RoleEntity> {
        protected MetaType() {
            super(RoleEntity.class, RoleId, RoleDescription);
        }

        @Override
        public RoleEntity newInstance() {
            return new RoleEntity();
        }
    }

    private RoleEntity() {

    }

    public RoleEntity(int roleId, String roleDescription) {
        this.roleId = roleId;
        this.roleDescription = roleDescription;
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

    public static Builder builder() {
        return new Builder();
    }
    public static RoleEntity create() {
        return new RoleEntity();
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
