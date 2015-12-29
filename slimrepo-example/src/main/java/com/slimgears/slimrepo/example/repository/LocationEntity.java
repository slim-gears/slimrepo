// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.example.repository;

import com.slimgears.slimrepo.core.annotations.Entity;

/**
 * Created by ditskovi on 12/29/2015.
 */
@Entity
public class LocationEntity {
    private int id;
    private CountryEntity country;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }
}
