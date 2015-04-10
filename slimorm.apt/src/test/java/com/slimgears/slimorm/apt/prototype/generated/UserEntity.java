// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.apt.prototype.generated;

import com.slimgears.slimorm.apt.prototype.AbstractUserEntity;
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
public class UserEntity extends AbstractUserEntity implements Entity<Integer> {
    public static class Fields {
        public static final EntityType<Integer, UserEntity> EntityMetaType;
        public static final NumberField<UserEntity, Integer> UserId = SlimSqlOrm.INSTANCE.createNumberField(UserEntity.class, "userId", Integer.class);
        public static final StringField<UserEntity> UserFirstName = SlimSqlOrm.INSTANCE.createStringField(UserEntity.class, "userFirstName");
        public static final StringField<UserEntity> UserLastName = SlimSqlOrm.INSTANCE.createStringField(UserEntity.class, "userLastName");

        static {
            EntityMetaType = new AbstractSqlEntityType<Integer, UserEntity>("UserEntity", UserEntity.class, UserId, UserId, UserFirstName, UserLastName) {
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
            };
        }
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
