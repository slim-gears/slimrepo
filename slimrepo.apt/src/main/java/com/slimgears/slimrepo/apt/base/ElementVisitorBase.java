package com.slimgears.slimrepo.apt.base;// Copyright 2015 Denis Itskovich
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
        R result = seedResult();

        if (element instanceof VariableElement) aggregateResult(result, visitVariable((VariableElement)element, p));
        if (element instanceof ExecutableElement) aggregateResult(result, visitExecutable((ExecutableElement)element, p));
        if (element instanceof TypeParameterElement) aggregateResult(result, visitTypeParameter((TypeParameterElement)element, p));
        if (element instanceof PackageElement) aggregateResult(result, visitPackage((PackageElement)element, p));
        if (element instanceof TypeElement) aggregateResult(result, visitType((TypeElement)element, p));

        return result;
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
