// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;
import com.slimgears.slimrepo.core.annotations.Relation;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
@GenerateEntity
public class AbstractUserEntity {
    protected @Key int id;
    protected String firstName;
    protected String lastName;
    protected int age;
    protected @Relation CountryEntity country;

    public String getFullName() {
        return lastName + ", " + firstName;
    }
}
