// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.interfaces.fields.BlobField;
import com.slimgears.slimrepo.core.interfaces.fields.RelationalField;
import com.slimgears.slimrepo.core.interfaces.fields.ValueGetter;
import com.slimgears.slimrepo.core.interfaces.fields.ValueSetter;
import com.slimgears.slimrepo.core.prototype.AbstractUserEntity;
import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueLookup;
import com.slimgears.slimrepo.core.interfaces.entities.FieldValueMap;
import com.slimgears.slimrepo.core.internal.Fields;
import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;

import java.util.ArrayList;
import java.util.Date;

public class UserEntity extends AbstractUserEntity {
    static class MetaType extends AbstractEntityType<String, UserEntity> {
        public MetaType() {
            //noinspection unchecked
            super(UserEntity.class, UserId, UserFirstName, UserLastName, LastVisitDate, Role, AccountStatus, Comments, Age);
        }

        @Override
        public UserEntity newInstance() {
            return new UserEntity();
        }
    }

    public static final StringField<UserEntity> UserId = Fields.stringField(
            "userId",
            UserEntity::getUserId,
            UserEntity::setUserId,
            false);
    public static final StringField<UserEntity> UserFirstName = Fields.stringField(
            "userFirstName",
            UserEntity::getUserFirstName,
            UserEntity::setUserFirstName,
            true);
    public static final StringField<UserEntity> UserLastName = Fields.stringField(
            "userLastName",
            UserEntity::getUserLastName,
            UserEntity::setUserLastName,
            true);
    public static final ComparableField<UserEntity, Date> LastVisitDate = Fields.comparableField(
            "lastVisitDate",
            Date.class,
            UserEntity::getLastVisitDate,
            UserEntity::setLastVisitDate,
            true);
    public static final RelationalField<UserEntity, RoleEntity> Role = Fields.relationalField(
            "role",
            RoleEntity.EntityMetaType,
            UserEntity::getRole,
            UserEntity::setRole,
            true);
    public static final ComparableField<UserEntity, AccountStatus> AccountStatus = Fields.comparableField(
            "accountStatus",
            AccountStatus.class,
            UserEntity::getAccountStatus,
            UserEntity::setAccountStatus,
            true);
    public static final BlobField<UserEntity, ArrayList> Comments = Fields.blobField(
            "comments",
            ArrayList.class,
            UserEntity::getComments,
            UserEntity::setComments,
            true);
    public static final ComparableField<UserEntity, Integer> Age = Fields.comparableField(
            "age",
            Integer.class,
            UserEntity::getAge,
            UserEntity::setAge,
            false);
    public static final EntityType<String, UserEntity> EntityMetaType = new MetaType();

    private UserEntity() {

    }

    public UserEntity(String userId, String userFirstName, String userLastName, Date lastVisitDate, RoleEntity role, AccountStatus accountStatus, ArrayList comments, int age) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.lastVisitDate = lastVisitDate;
        this.accountStatus = accountStatus;
        this.role = role;
        this.comments = comments;
        this.age = age;
    }

    public static class Builder {
        private UserEntity model = new UserEntity();

        public Builder userId(String id) {
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

        public Builder role(RoleEntity role) {
            model.setRole(role);
            return this;
        }

        public Builder accountStatus(AccountStatus accountStatus) {
            model.setAccountStatus(accountStatus);
            return this;
        }

        public Builder comments(ArrayList<String> comments) {
            model.setComments(comments);
            return this;
        }

        public Builder age(int age) {
            model.setAge(age);
            return this;
        }

        public UserEntity build() {
            return model;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static UserEntity create() {
        return new UserEntity();
    }

    public String getUserId() {
        return userId;
    }

    public UserEntity setUserId(String userId) {
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

    public RoleEntity getRole() {
        return (RoleEntity)role;
    }

    public UserEntity setRole(RoleEntity role) {
        this.role = role;
        return this;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public UserEntity setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
        return this;
    }

    public ArrayList getComments() {
        return this.comments;
    }

    public UserEntity setComments(ArrayList<String> comments) {
        this.comments = comments;
        return this;
    }

    public int getAge() {
        return this.age;
    }

    public UserEntity setAge(int age) {
        this.age = age;
        return this;
    }
}
