// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.prototype.AbstractUserEntity;
import com.slimgears.slimrepo.core.interfaces.entities.Entity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.interfaces.fields.Fields;
import com.slimgears.slimrepo.core.interfaces.fields.NumericField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;

import java.util.Date;

public class UserEntity extends AbstractUserEntity implements Entity<Integer> {
    static class MetaType extends AbstractEntityType<Integer, UserEntity> {
        public MetaType() {
            super("UserEntity", UserEntity.class, UserId, UserFirstName, UserLastName, LastVisitDate);
        }

        @Override
        public UserEntity newInstance() {
            return new UserEntity();
        }

        @Override
        public UserEntity newInstance(FieldValueLookup<UserEntity> lookup) {
            return new UserEntity(
                    lookup.getValue(UserId),
                    lookup.getValue(UserFirstName),
                    lookup.getValue(UserLastName),
                    lookup.getValue(LastVisitDate));
        }

        @Override
        public void entityToMap(UserEntity entity, FieldValueMap<UserEntity> map) {
            map
                    .putValue(UserId, entity.getUserId())
                    .putValue(UserFirstName, entity.getUserFirstName())
                    .putValue(UserLastName, entity.getUserLastName())
                    .putValue(LastVisitDate, entity.getLastVisitDate());
        }

        @Override
        public void setKey(UserEntity entity, Integer key) {
            entity.setUserId(key);
        }
    }

    public static final NumericField<UserEntity, Integer> UserId = Fields.numberField("userId", UserEntity.class, Integer.class, false);
    public static final StringField<UserEntity> UserFirstName = Fields.stringField("userFirstName", UserEntity.class, true);
    public static final StringField<UserEntity> UserLastName = Fields.stringField("userLastName", UserEntity.class, true);
    public static final NumericField<UserEntity, Date> LastVisitDate = Fields.dateField("lastVisitDate", UserEntity.class, true);
    public static final EntityType<Integer, UserEntity> EntityMetaType = new MetaType();

    private UserEntity() {

    }

    public UserEntity(int userId, String userFirstName, String userLastName, Date lastVisitDate) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.lastVisitDate = lastVisitDate;
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

        public Builder lastVisitDate(Date lastVisitDate) {
            model.setLastVisitDate(lastVisitDate);
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

    public Date getLastVisitDate() {
        return lastVisitDate;
    }

    public UserEntity setLastVisitDate(Date lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
        return this;
    }
}
