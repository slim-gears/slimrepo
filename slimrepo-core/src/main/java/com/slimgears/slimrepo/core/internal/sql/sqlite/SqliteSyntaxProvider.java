// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.internal.sql.sqlite;

import com.slimgears.slimrepo.core.interfaces.entities.EntityType;
import com.slimgears.slimrepo.core.interfaces.fields.Field;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;
import com.slimgears.slimrepo.core.internal.interfaces.RepositoryModel;
import com.slimgears.slimrepo.core.internal.sql.AbstractSqlSyntaxProvider;

/**
 * Created by Denis on 11-Apr-15
 * <File Description>
 */
public class SqliteSyntaxProvider extends AbstractSqlSyntaxProvider {
    public SqliteSyntaxProvider(FieldTypeMappingRegistrar mappingRegistrar) {
        super(mappingRegistrar);
    }

    @Override
    public String simpleFieldName(Field field) {
        return '`' + field.metaInfo().getName() + '`';
    }

    @Override
    public String tableName(EntityType entityType) {
        return '`' + entityType.getName() + '`';
    }

    @Override
    public String databaseName(RepositoryModel repositoryModel) {
        return repositoryModel.getName() + ".db";
    }

    @Override
    public String parameterReference(int index, String name) {
        return "?";
    }

    @Override
    public <T> String valueToString(Field<?, T> field, T value) {
        if (value == null) return "NULL";
        return fieldTypeMapper.fromFieldType(field, value).toString();
    }

    @Override
    public String rawFieldAlias(Field<?, ?> field) {
        Field.MetaInfo metaInfo = field.metaInfo();
        return metaInfo.getEntityType().getName() + "_" + metaInfo.getName();
    }
}
