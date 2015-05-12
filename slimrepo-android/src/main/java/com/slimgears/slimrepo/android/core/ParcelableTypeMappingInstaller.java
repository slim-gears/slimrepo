package com.slimgears.slimrepo.android.core;

import android.content.Intent;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
 * Created by Denis on 09-May-15.
 */
public class ParcelableTypeMappingInstaller implements FieldTypeMappingInstaller {
    @Override
    public void install(FieldTypeMappingRegistrar registrar) {
        ParcelableTypeConverter.install(registrar, Bundle.class, Bundle.CREATOR);
        ParcelableTypeConverter.install(registrar, Bitmap.class, Bitmap.CREATOR);
        ParcelableTypeConverter.install(registrar, Address.class, Address.CREATOR);
        ParcelableTypeConverter.install(registrar, Location.class, Location.CREATOR);
        ParcelableTypeConverter.install(registrar, Intent.class, Intent.CREATOR);
        ParcelableTypeConverter.install(registrar, Gesture.class, Gesture.CREATOR);
        ParcelableTypeConverter.install(registrar, Uri.class, Uri.CREATOR);
    }
}
