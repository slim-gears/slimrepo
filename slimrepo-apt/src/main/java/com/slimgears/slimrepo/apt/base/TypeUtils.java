package com.slimgears.slimrepo.apt.base;

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
        String name = begin.length() > 0
                ? Character.toLowerCase(begin.charAt(0)) + begin.substring(1)
                : begin;

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
            return Collections2.transform(Arrays.asList(getter.getTypes(annotation)), TypeName::get);
        } catch (MirroredTypesException e) {
            return Collections2.transform(e.getTypeMirrors(), TypeName::get);
        }
    }

    public static TypeName getTypeName(final TypeMirror typeMirror) {
        try {
            return TypeName.get(typeMirror);
        } catch (Exception e) {
            return ClassName.get(TypeUtils.packageName(typeMirror.toString()), TypeUtils.simpleName(typeMirror.toString()));
        }
    }

    public static TypeName getTypeName(TypeMirror typeMirror, String defaultPackageName) {
        String typePackage = TypeUtils.packageName(typeMirror.toString());
        if (typePackage.isEmpty()) typePackage = defaultPackageName;

        try {
            return TypeName.get(typeMirror);
        } catch (Exception e) {
            return ClassName.get(typePackage, TypeUtils.simpleName(typeMirror.toString()));
        }
    }

    public static TypeName box(TypeName type) {
        if (type == TypeName.INT) return TypeName.get(Integer.class);
        if (type == TypeName.SHORT) return TypeName.get(Short.class);
        if (type == TypeName.LONG) return TypeName.get(Long.class);
        if (type == TypeName.BOOLEAN) return TypeName.get(Boolean.class);
        if (type == TypeName.DOUBLE) return TypeName.get(Double.class);
        if (type == TypeName.FLOAT) return TypeName.get(Float.class);
        if (type == TypeName.BYTE) return TypeName.get(Byte.class);
        return type;
    }
}
