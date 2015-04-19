// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.core.internal.sql;

import com.slimgears.slimorm.core.internal.interfaces.SessionServiceProvider;

/**
 * Created by Denis on 09-Apr-15
 * <File Description>
 */
public interface SqlSessionServiceProvider extends SessionServiceProvider {
    SqlCommandExecutor getExecutor();
    SqlStatementBuilder getStatementBuilder();
    SqlStatementBuilder.SyntaxProvider getSyntaxProvider();
}
