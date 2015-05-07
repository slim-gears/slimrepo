package com.slimgears.slimrepo.android.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.slimgears.slimrepo.core.internal.AbstractTypeConverter;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
 * Created by Denis on 05-May-15.
 */
public class ParcelableTypeConverter<T extends Parcelable> extends AbstractTypeConverter<T, byte[]> {
    private final Parcelable.Creator<T> creator;

    public ParcelableTypeConverter(Class<T> sourceType, Parcelable.Creator<T> creator) {
        super(sourceType, byte[].class);
        this.creator = creator;
    }

    @Override
    protected T fromInbound(byte[] data) {
        Parcel parcel = Parcel.obtain();
        try {
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return creator.createFromParcel(parcel);
        } finally {
            parcel.recycle();
        }
    }

    @Override
    protected byte[] toOutbound(T value) {
        Parcel parcel = Parcel.obtain();
        try {
            value.writeToParcel(parcel, 0);
            return parcel.marshall();
        } finally {
            parcel.recycle();
        }
    }

    public static <T extends Parcelable> FieldTypeMappingRegistrar.TypeConverter<T> create(Class<T> clazz, Parcelable.Creator<T> creator) {
        return new ParcelableTypeConverter<>(clazz, creator);
    }
}
