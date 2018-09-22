// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.interfaces;

import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

/**
 * Created by Denis on 14-Apr-15
 *
 */
public interface SqlOrmServiceProvider extends OrmServiceProvider {
    SqlStatementBuilder getStatementBuilder();
    SqlStatementBuilder.SyntaxProvider getSyntaxProvider();
}
