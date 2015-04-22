// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import com.slimgears.slimrepo.core.annotations.GenerateEntity;
import com.slimgears.slimrepo.core.annotations.Key;

/**
 * Created by Denis on 22-Apr-15
 * <File Description>
 */
@GenerateEntity
public class AbstractCountryEntity {
    @Key protected int id;
    protected String name;
}
