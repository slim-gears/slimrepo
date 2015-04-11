// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.android.prototype.core;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Denis on 10-Apr-15
 * <File Description>
 */
public interface SqliteDatabaseProvider {
    SQLiteDatabase getDatabase();
}
