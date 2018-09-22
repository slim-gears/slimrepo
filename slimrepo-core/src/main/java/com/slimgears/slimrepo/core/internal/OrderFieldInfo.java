// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
* Created by Denis on 08-Apr-15
*
*/
public class OrderFieldInfo {
    public final boolean ascending;
    public final Field field;

    public OrderFieldInfo(Field field, boolean ascending) {
        this.ascending = ascending;
        this.field = field;
    }
}
