package com.slimgears.slimorm.apt.base;// Copyright 2015 Denis Itskovich
// Refer to LICENSE.txt for license details

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by Denis on 03-Apr-15
 * <File Description>
 */
public class ElementVisitorBase<R, P> implements ElementVisitor<R, P> {
    @Override
    public R visit(Element element, P p) {
        if (element instanceof VariableElement) return visitVariable((VariableElement)element, p);
        else if (element instanceof ExecutableElement) return visitExecutable((ExecutableElement)element, p);
        else if (element instanceof TypeParameterElement) return visitTypeParameter((TypeParameterElement)element, p);
        else if (element instanceof PackageElement) return visitPackage((PackageElement)element, p);
        else if (element instanceof TypeElement) return visitType((TypeElement)element, p);
        else return visitUnknown(element, p);
    }

    @Override
    public R visit(Element element) {
        return visit(element, null);
    }

    @Override
    public R visitPackage(PackageElement e, P p) {
        return null;
    }

    @Override
    public R visitType(TypeElement e, P p) {
        R result = seedResult();
        for (Element element : e.getEnclosedElements()) {
            result = aggregateResult(result, visit(element, p));
        }
        return result;
    }

    @Override
    public R visitVariable(VariableElement e, P p) {
        return null;
    }

    @Override
    public R visitExecutable(ExecutableElement e, P p) {
        return null;
    }

    @Override
    public R visitTypeParameter(TypeParameterElement e, P p) {
        return null;
    }

    @Override
    public R visitUnknown(Element e, P p) {
        return null;
    }

    protected R seedResult() {
        return null;
    }

    protected R aggregateResult(R previous, R current) {
        return current;
    }
}
