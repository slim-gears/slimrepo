// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimorm.internal.sql;

import com.slimgears.slimorm.interfaces.EntityType;
import com.slimgears.slimorm.interfaces.fields.Field;
import com.slimgears.slimorm.interfaces.predicates.Predicate;
import com.slimgears.slimorm.interfaces.FieldValueLookup;
import com.slimgears.slimorm.internal.OrderFieldInfo;
import com.slimgears.slimorm.internal.UpdateFieldInfo;

import java.util.Collection;

/**
 * Created by Denis on 08-Apr-15
 * <File Description>
 */
public interface SqlStatementBuilder {
    interface SyntaxProvider {
        String fieldName(Field field);
        String tableName(EntityType entityType);
        String parameterReference(int index, String name);
        String valueToString(Object value);
    }

    interface PredicateBuilder {
        <T> String build(Predicate<T> predicate, SqlCommand.Parameters parameters);
    }

    class BaseParameters<TParams extends BaseParameters<TParams>> {
        public EntityType entityType;
        public SqlCommand.Parameters commandParameters;

        protected TParams self() {
            //noinspection unchecked
            return (TParams)this;
        }

        public TParams setEntityType(EntityType entityType) {
            this.entityType = entityType;
            return self();
        }

        public TParams setCommandParameters(SqlCommand.Parameters params) {
            this.commandParameters = params;
            return self();
        }
    }

    class ConditionalParameters<TParams extends ConditionalParameters<TParams>> extends BaseParameters<TParams> {
        public Predicate predicate;
        public int limit = -1;
        public int offset = 0;

        public TParams setPredicate(Predicate predicate) {
            this.predicate = predicate;
            return self();
        }

        public TParams setLimit(int limit) {
            this.limit = limit;
            return self();
        }

        public TParams setOffset(int offset) {
            this.offset = offset;
            return self();
        }
    }

    class SelectParameters extends ConditionalParameters<SelectParameters> {
        public Collection<OrderFieldInfo> orderFields;

        public SelectParameters setOrderFields(Collection<OrderFieldInfo> orderFields) {
            this.orderFields = orderFields;
            return self();
        }
    }

    class DeleteParameters extends ConditionalParameters<DeleteParameters> {

    }

    class CountParameters extends ConditionalParameters<CountParameters> {

    }

    class UpdateParameters extends ConditionalParameters<UpdateParameters> {
        public Collection<UpdateFieldInfo> updateFields;

        public UpdateParameters setUpdateFields(Collection<UpdateFieldInfo> updateFields) {
            this.updateFields = updateFields;
            return self();
        }
    }

    class InsertParameters extends BaseParameters<InsertParameters> {
        public Collection<FieldValueLookup> rows;

        public InsertParameters setRows(Collection<FieldValueLookup> rows) {
            this.rows = rows;
            return self();
        }
    }

    class CreateTableParameters extends BaseParameters<CreateTableParameters> {
    }

    class DropTableParameters extends BaseParameters<DropTableParameters> {
    }

    String buildCountStatement(CountParameters params);
    String buildSelectStatement(SelectParameters params);
    String buildUpdateStatement(UpdateParameters params);
    String buildDeleteStatement(DeleteParameters params);
    String buildInsertStatement(InsertParameters params);
    String buildCreateTableStatement(CreateTableParameters params);
    String buildDropTableStatement(DropTableParameters params);
}
