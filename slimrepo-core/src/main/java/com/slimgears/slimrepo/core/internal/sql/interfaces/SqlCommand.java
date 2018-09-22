// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import java.util.Map;

/**
* Created by Denis on 08-Apr-15
*
*/
public interface SqlCommand {
    String getStatement();
    Parameters getParameters();

    interface Parameters {
        String add(String parameter);
        int getCount();
        Map<String, String> getMap();
        String[] getValues();
    }
}
