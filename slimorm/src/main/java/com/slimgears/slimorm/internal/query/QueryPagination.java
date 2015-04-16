// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.query;

/**
* Created by Denis on 13-Apr-15
* <File Description>
*/
public class QueryPagination {
    public int limit = -1;
    public int offset = 0;

    public QueryPagination fork() {
        QueryPagination pagination = new QueryPagination();
        pagination.limit = limit;
        pagination.offset = offset;
        return pagination;
    }
}
