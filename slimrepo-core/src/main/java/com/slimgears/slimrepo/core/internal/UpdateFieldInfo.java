// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal;

import com.slimgears.slimrepo.core.interfaces.fields.Field;

/**
 * Created by Denis on 08-Apr-15
 *
 */
public class UpdateFieldInfo {
    public final Field field;
    public final Object value;

    public UpdateFieldInfo(Field field, Object value) {
        this.field = field;
        this.value = value;
    }
}
