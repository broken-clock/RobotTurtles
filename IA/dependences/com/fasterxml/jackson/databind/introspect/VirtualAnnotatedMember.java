// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import com.fasterxml.jackson.databind.JavaType;
import java.io.Serializable;

public class VirtualAnnotatedMember extends AnnotatedMember implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _declaringClass;
    protected final JavaType _type;
    protected final String _name;
    
    public VirtualAnnotatedMember(final TypeResolutionContext typeContext, final Class<?> declaringClass, final String name, final JavaType type) {
        super(typeContext, null);
        this._declaringClass = declaringClass;
        this._type = type;
        this._name = name;
    }
    
    @Override
    public Annotated withAnnotations(final AnnotationMap fallback) {
        return this;
    }
    
    @Override
    public Field getAnnotated() {
        return null;
    }
    
    public int getModifiers() {
        return 0;
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    @Override
    public Class<?> getRawType() {
        return this._type.getRawClass();
    }
    
    @Override
    public JavaType getType() {
        return this._type;
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this._declaringClass;
    }
    
    @Override
    public Member getMember() {
        return null;
    }
    
    @Override
    public void setValue(final Object pojo, final Object value) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot set virtual property '" + this._name + "'");
    }
    
    @Override
    public Object getValue(final Object pojo) throws IllegalArgumentException {
        throw new IllegalArgumentException("Cannot get virtual property '" + this._name + "'");
    }
    
    public int getAnnotationCount() {
        return 0;
    }
    
    @Override
    public int hashCode() {
        return this._name.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!ClassUtil.hasClass(o, this.getClass())) {
            return false;
        }
        final VirtualAnnotatedMember other = (VirtualAnnotatedMember)o;
        return other._declaringClass == this._declaringClass && other._name.equals(this._name);
    }
    
    @Override
    public String toString() {
        return "[virtual " + this.getFullName() + "]";
    }
}
