// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import android.os.Bundle;
import com.slimgears.slimrepo.core.annotations.GenerateEntity;

/**
 * Created by Denis on 22-Apr-15
 *
 */
@GenerateEntity
public class AbstractUserEntity {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected int age;
    protected AbstractCountryEntity country;
    protected Bundle status;

    public String getFullName() {
        return lastName + ", " + firstName;
    }
}
