// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;
import com.fasterxml.jackson.databind.JavaType;

public final class AnnotatedParameter extends AnnotatedMember
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedWithParams _owner;
    protected final JavaType _type;
    protected final int _index;
    
    public AnnotatedParameter(final AnnotatedWithParams owner, final JavaType type, final TypeResolutionContext typeContext, final AnnotationMap annotations, final int index) {
        super(typeContext, annotations);
        this._owner = owner;
        this._type = type;
        this._index = index;
    }
    
    @Override
    public AnnotatedParameter withAnnotations(final AnnotationMap ann) {
        if (ann == this._annotations) {
            return this;
        }
        return this._owner.replaceParameterAnnotations(this._index, ann);
    }
    
    @Override
    public AnnotatedElement getAnnotated() {
        return null;
    }
    
    public int getModifiers() {
        return this._owner.getModifiers();
    }
    
    @Override
    public String getName() {
        return "";
    }
    
    @Override
    public Class<?> getRawType() {
        return this._type.getRawClass();
    }
    
    @Override
    public JavaType getType() {
        return this._type;
    }
    
    @Deprecated
    @Override
    public Type getGenericType() {
        return this._owner.getGenericParameterType(this._index);
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this._owner.getDeclaringClass();
    }
    
    @Override
    public Member getMember() {
        return this._owner.getMember();
    }
    
    @Override
    public void setValue(final Object pojo, final Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call setValue() on constructor parameter of " + this.getDeclaringClass().getName());
    }
    
    @Override
    public Object getValue(final Object pojo) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call getValue() on constructor parameter of " + this.getDeclaringClass().getName());
    }
    
    public Type getParameterType() {
        return this._type;
    }
    
    public AnnotatedWithParams getOwner() {
        return this._owner;
    }
    
    public int getIndex() {
        return this._index;
    }
    
    @Override
    public int hashCode() {
        return this._owner.hashCode() + this._index;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!ClassUtil.hasClass(o, this.getClass())) {
            return false;
        }
        final AnnotatedParameter other = (AnnotatedParameter)o;
        return other._owner.equals(this._owner) && other._index == this._index;
    }
    
    @Override
    public String toString() {
        return "[parameter #" + this.getIndex() + ", annotations: " + this._annotations + "]";
    }
}
