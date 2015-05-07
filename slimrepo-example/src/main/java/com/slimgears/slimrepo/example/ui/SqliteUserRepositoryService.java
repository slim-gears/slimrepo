package com.slimgears.slimrepo.example.ui;

import android.content.Context;

import com.slimgears.slimrepo.android.core.SqliteOrmServiceProvider;
import com.slimgears.slimrepo.example.repository.GeneratedUserRepositoryService;

/**
 * Created by Denis on 05-May-15.
 */
public class SqliteUserRepositoryService extends GeneratedUserRepositoryService {
    public SqliteUserRepositoryService(Context context) {
        super(new SqliteOrmServiceProvider(context));
    }
}
