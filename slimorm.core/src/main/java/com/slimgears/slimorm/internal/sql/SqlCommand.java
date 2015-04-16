// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import java.util.Map;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
public interface SqlCommand {
    String getStatement();
    Parameters getParameters();

    interface Parameters {
        String add(Object parameter);
        int getCount();
        Map<String, Object> getMap();
        Object[] getValues();
    }
}
