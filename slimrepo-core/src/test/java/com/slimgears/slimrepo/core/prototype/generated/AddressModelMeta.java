// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details
package com.slimgears.slimrepo.core.prototype.generated;

import com.slimgears.slimrepo.core.interfaces.fields.ComparableField;
import com.slimgears.slimrepo.core.interfaces.fields.StringField;
import com.slimgears.slimrepo.core.internal.AbstractEntityType;
import com.slimgears.slimrepo.core.internal.Fields;
import com.slimgears.slimrepo.core.prototype.AddressModel;

/**
 * Created by ditskovi on 12/22/2015.
 */
public class AddressModelMeta {
    public static final ComparableField<AddressModel, Integer> Id = Fields.comparableField("id", Integer.class, AddressModel::getId, AddressModel::setId, false);
    public static final StringField<AddressModel> City = Fields.stringField("city", AddressModel::getCity, AddressModel::setCity, true);
    public static final StringField<AddressModel> Country = Fields.stringField("country", AddressModel::getCountry, AddressModel::setCountry, true);
    public static final StringField<AddressModel> StreetAddress = Fields.stringField("streetAddress", AddressModel::getStreetAddress, AddressModel::setStreetAddress, true);

    class MetaType extends AbstractEntityType<Integer, AddressModel> {
        protected MetaType() { super(AddressModel.class, Id, City, Country, StreetAddress); }

        @Override
        public AddressModel newInstance() {
            return new AddressModel();
        }
    }
}
