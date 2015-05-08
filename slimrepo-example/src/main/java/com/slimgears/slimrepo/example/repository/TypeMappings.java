package com.slimgears.slimrepo.example.repository;

import android.graphics.Rect;

import com.slimgears.slimrepo.android.core.ParcelableTypeConverter;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingRegistrar;

/**
 * Created by Denis on 08-May-15.
 */
public class TypeMappings implements FieldTypeMappingInstaller {
    @Override
    public void install(FieldTypeMappingRegistrar registrar) {
        ParcelableTypeConverter.install(registrar, Rect.class, Rect.CREATOR);
    }
}
