package com.slimgears.slimrepo.apt.base;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

/**
 * Created by Denis on 08-May-15.
 */
public class TypeUtils {
    public static String packageName(String qualifiedClassName) {
        int pos = qualifiedClassName.lastIndexOf('.');
        return (pos >= 0) ? qualifiedClassName.substring(0, pos) : "";
    }

    public static String simpleName(String qualifiedClassName) {
        String packageName = packageName(qualifiedClassName);
        return packageName.isEmpty() ? qualifiedClassName : qualifiedClassName.substring(packageName.length() + 1);
    }

    public static String qualifiedName(String packageName, String simpleName) {
        return packageName.isEmpty() ? simpleName : packageName + "." + simpleName;
    }

    public static ClassName getClassName(String qualifiedClassName) {
        return ClassName.get(packageName(qualifiedClassName), simpleName(qualifiedClassName));
    }

    public static String toCamelCase(String begin, String... parts) {
        String name = begin;
        for (String part : parts) {
            name += Character.toUpperCase(part.charAt(0)) + part.substring(1);
        }
        return name;
    }

    public interface AnnotationTypesGetter<TAnnotation extends Annotation> {
        Class[] getTypes(TAnnotation annotation) throws MirroredTypesException;
    }

    public interface AnnotationTypeGetter<TAnnotation extends Annotation> {
        Class getType(TAnnotation annotation) throws MirroredTypeException;
    }

    public static <TAnnotation extends Annotation> TypeName getTypeFromAnnotation(TAnnotation annotation, AnnotationTypeGetter<TAnnotation> getter) {
        try {
            return TypeName.get(getter.getType(annotation));
        } catch (MirroredTypeException e) {
            return TypeName.get(e.getTypeMirror());
        }
    }

    public static <TAnnotation extends Annotation> Collection<TypeName> getTypesFromAnnotation(TAnnotation annotation, AnnotationTypesGetter<TAnnotation> getter) {
        try {
            return Collections2.transform(Arrays.asList(getter.getTypes(annotation)), new Function<Class, TypeName>() {
                @Override
                public TypeName apply(Class input) {
                    return TypeName.get(input);
                }
            });
        } catch (MirroredTypesException e) {
            return Collections2.transform(e.getTypeMirrors(), new Function<TypeMirror, TypeName>() {
                @Override
                public TypeName apply(TypeMirror input) {
                    return TypeName.get(input);
                }
            });
        }
    }
}
