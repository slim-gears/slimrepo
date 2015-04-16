// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.generated;

import com.slimgears.slimorm.android.prototype.AbstractUserEntity;
import com.slimgears.slimorm.interfaces.entities.Entity;
import com.slimgears.slimorm.interfaces.entities.EntityType;
import com.slimgears.slimorm.interfaces.entities.FieldValueLookup;
import com.slimgears.slimorm.interfaces.entities.FieldValueMap;
import com.slimgears.slimorm.interfaces.fields.Fields;
import com.slimgears.slimorm.interfaces.fields.NumberField;
import com.slimgears.slimorm.interfaces.fields.StringField;
import com.slimgears.slimorm.internal.AbstractEntityType;

/**
 * Created by Denis on 05-Apr-15
 * <File Description>
 */
public class UserEntity extends AbstractUserEntity implements Entity<Integer> {
    static class MetaType extends AbstractEntityType<Integer, UserEntity> {
        public MetaType() {
            super("UserEntity", UserEntity.class, UserId);
        }

        @Override
        public UserEntity newInstance() {
            return new UserEntity();
        }

        @Override
        public UserEntity newInstance(FieldValueLookup<UserEntity> lookup) {
            return newInstance()
                    .setUserId(lookup.getValue(UserId))
                    .setUserFirstName(lookup.getValue(UserFirstName))
                    .setUserLastName(lookup.getValue(UserLastName));
        }

        @Override
        public void entityToMap(UserEntity entity, FieldValueMap<UserEntity> map) {
            map
                    .putValue(UserId, entity.getUserId())
                    .putValue(UserFirstName, entity.getUserFirstName())
                    .putValue(UserLastName, entity.getUserLastName());
        }
    }

    public static final EntityType<Integer, UserEntity> EntityMetaType;
    public static final NumberField<UserEntity, Integer> UserId = Fields.numberField(UserEntity.class, "userId", Integer.class);
    public static final StringField<UserEntity> UserFirstName = Fields.stringField(UserEntity.class, "userFirstName");
    public static final StringField<UserEntity> UserLastName = Fields.stringField(UserEntity.class, "userLastName");

    static {
        EntityMetaType = new MetaType()
                .addFields(UserFirstName, UserLastName)
                .addRelatedEntities();
    }

    @Override
    public Integer getEntityId() {
        return getUserId();
    }

    public static class Builder {
        private UserEntity model = new UserEntity();

        public Builder userId(int id) {
            model.setUserId(id);
            return this;
        }

        public Builder userFirstName(String firstName) {
            model.setUserFirstName(firstName);
            return this;
        }

        public Builder userLastName(String lastName) {
            model.setUserLastName(lastName);
            return this;
        }

        public UserEntity build() {
            return model;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public int getUserId() {
        return userId;
    }

    public UserEntity setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public UserEntity setUserFirstName(String name) {
        this.userFirstName = name;
        return this;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public UserEntity setUserLastName(String lastName) {
        this.userLastName = lastName;
        return this;
    }
}
