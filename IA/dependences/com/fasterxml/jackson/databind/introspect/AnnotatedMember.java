// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.Collections;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.io.Serializable;

public abstract class AnnotatedMember extends Annotated implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient TypeResolutionContext _typeContext;
    protected final transient AnnotationMap _annotations;
    
    protected AnnotatedMember(final TypeResolutionContext ctxt, final AnnotationMap annotations) {
        this._typeContext = ctxt;
        this._annotations = annotations;
    }
    
    protected AnnotatedMember(final AnnotatedMember base) {
        this._typeContext = base._typeContext;
        this._annotations = base._annotations;
    }
    
    public abstract Annotated withAnnotations(final AnnotationMap p0);
    
    public abstract Class<?> getDeclaringClass();
    
    public abstract Member getMember();
    
    public String getFullName() {
        return this.getDeclaringClass().getName() + "#" + this.getName();
    }
    
    @Deprecated
    public TypeResolutionContext getTypeContext() {
        return this._typeContext;
    }
    
    @Override
    public final <A extends Annotation> A getAnnotation(final Class<A> acls) {
        if (this._annotations == null) {
            return null;
        }
        return this._annotations.get(acls);
    }
    
    @Override
    public final boolean hasAnnotation(final Class<?> acls) {
        return this._annotations != null && this._annotations.has(acls);
    }
    
    @Override
    public boolean hasOneOf(final Class<? extends Annotation>[] annoClasses) {
        return this._annotations != null && this._annotations.hasOneOf(annoClasses);
    }
    
    @Deprecated
    @Override
    public Iterable<Annotation> annotations() {
        if (this._annotations == null) {
            return (Iterable<Annotation>)Collections.emptyList();
        }
        return this._annotations.annotations();
    }
    
    public AnnotationMap getAllAnnotations() {
        return this._annotations;
    }
    
    public final void fixAccess(final boolean force) {
        final Member m = this.getMember();
        if (m != null) {
            ClassUtil.checkAndFixAccess(m, force);
        }
    }
    
    public abstract void setValue(final Object p0, final Object p1) throws UnsupportedOperationException, IllegalArgumentException;
    
    public abstract Object getValue(final Object p0) throws UnsupportedOperationException, IllegalArgumentException;
}
