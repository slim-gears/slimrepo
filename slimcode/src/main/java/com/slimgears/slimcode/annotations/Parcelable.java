// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

package com.slimgears.slimcode.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Denis on 29-Mar-15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Parcelable {
}
