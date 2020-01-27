// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.annotation.Annotation;

public abstract class AnnotatedWithParams extends AnnotatedMember
{
    private static final long serialVersionUID = 1L;
    protected final AnnotationMap[] _paramAnnotations;
    
    protected AnnotatedWithParams(final TypeResolutionContext ctxt, final AnnotationMap annotations, final AnnotationMap[] paramAnnotations) {
        super(ctxt, annotations);
        this._paramAnnotations = paramAnnotations;
    }
    
    protected AnnotatedWithParams(final AnnotatedWithParams base, final AnnotationMap[] paramAnnotations) {
        super(base);
        this._paramAnnotations = paramAnnotations;
    }
    
    public final void addOrOverrideParam(final int paramIndex, final Annotation a) {
        AnnotationMap old = this._paramAnnotations[paramIndex];
        if (old == null) {
            old = new AnnotationMap();
            this._paramAnnotations[paramIndex] = old;
        }
        old.add(a);
    }
    
    protected AnnotatedParameter replaceParameterAnnotations(final int index, final AnnotationMap ann) {
        this._paramAnnotations[index] = ann;
        return this.getParameter(index);
    }
    
    public final AnnotationMap getParameterAnnotations(final int index) {
        if (this._paramAnnotations != null && index >= 0 && index < this._paramAnnotations.length) {
            return this._paramAnnotations[index];
        }
        return null;
    }
    
    public final AnnotatedParameter getParameter(final int index) {
        return new AnnotatedParameter(this, this.getParameterType(index), this._typeContext, this.getParameterAnnotations(index), index);
    }
    
    public abstract int getParameterCount();
    
    public abstract Class<?> getRawParameterType(final int p0);
    
    public abstract JavaType getParameterType(final int p0);
    
    @Deprecated
    public abstract Type getGenericParameterType(final int p0);
    
    public final int getAnnotationCount() {
        return this._annotations.size();
    }
    
    public abstract Object call() throws Exception;
    
    public abstract Object call(final Object[] p0) throws Exception;
    
    public abstract Object call1(final Object p0) throws Exception;
}
