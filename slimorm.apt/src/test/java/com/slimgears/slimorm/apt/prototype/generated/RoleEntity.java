// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.generated;

import com.slimgears.slimorm.apt.prototype.AbstractRoleEntity;
import com.slimgears.slimorm.apt.prototype.slimsql.SlimSqlOrm;
import com.slimgears.slimorm.interfaces.Entity;
import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.FieldValueMap;
import com.slimgears.slimorm.interfaces.NumberField;
import com.slimgears.slimorm.interfaces.StringField;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.sql.AbstractSqlEntityType;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class RoleEntity extends AbstractRoleEntity implements Entity<Integer> {
    public static class Fields {
        public static final EntityType<Integer, RoleEntity> EntityMetaType;
        public static final NumberField<RoleEntity, Integer> RoleId = SlimSqlOrm.INSTANCE.createNumberField(RoleEntity.class, "roleId", Integer.class);
        public static final StringField<RoleEntity> RoleDescription = SlimSqlOrm.INSTANCE.createStringField(RoleEntity.class, "roleDescription");
        static {
            EntityMetaType = new AbstractSqlEntityType<Integer, RoleEntity>("RoleEntity", RoleEntity.class, RoleId, RoleId, RoleDescription) {
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
            };
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
