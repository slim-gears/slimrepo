package com.slimgears.slimrepo.core.annotations;

import com.slimgears.slimrepo.core.internal.interfaces.FieldTypeMappingInstaller;
import com.slimgears.slimrepo.core.internal.interfaces.OrmServiceProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Denis on 08-May-15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface OrmProvider {
    Class<? extends OrmServiceProvider> value();
    Class<? extends FieldTypeMappingInstaller>[] typeMappings() default {};
}
