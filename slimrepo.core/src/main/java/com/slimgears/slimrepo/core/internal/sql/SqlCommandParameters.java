// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Created by Denis on 08-Apr-15
* <File Description>
*/
class SqlCommandParameters implements SqlCommand.Parameters {
    private final List<String> parameters = new ArrayList<>();
    private final Map<String, String> parameterMap = new HashMap<>();

    @Override
    public String add(String value) {
        parameters.add(value);
        String paramName = "@p" + parameters.size();
        parameterMap.put(paramName, value);
        return paramName;
    }

    @Override
    public int getCount() {
        return parameters.size();
    }

    @Override
    public Map<String, String> getMap() {
        return parameterMap;
    }

    @Override
    public String[] getValues() {
        return parameters.toArray(new String[parameters.size()]);
    }
}
