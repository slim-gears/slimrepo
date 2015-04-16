// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal;

import com.slimgears.slimorm.interfaces.fields.Field;

import java.util.Collection;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public class OrderFieldInfo {
    public final boolean ascending;
    public final Field field;

    public OrderFieldInfo(Field field, boolean ascending) {
        this.ascending = ascending;
        this.field = field;
    }
}
