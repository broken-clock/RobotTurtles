// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.AnnotatedElement;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Modifier;
import java.lang.reflect.Member;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Type;
import java.lang.reflect.Field;
import java.io.Serializable;

public final class AnnotatedField extends AnnotatedMember implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient Field _field;
    protected Serialization _serialization;
    
    public AnnotatedField(final TypeResolutionContext contextClass, final Field field, final AnnotationMap annMap) {
        super(contextClass, annMap);
        this._field = field;
    }
    
    @Override
    public AnnotatedField withAnnotations(final AnnotationMap ann) {
        return new AnnotatedField(this._typeContext, this._field, ann);
    }
    
    protected AnnotatedField(final Serialization ser) {
        super(null, null);
        this._field = null;
        this._serialization = ser;
    }
    
    @Override
    public Field getAnnotated() {
        return this._field;
    }
    
    public int getModifiers() {
        return this._field.getModifiers();
    }
    
    @Override
    public String getName() {
        return this._field.getName();
    }
    
    @Override
    public Class<?> getRawType() {
        return this._field.getType();
    }
    
    @Deprecated
    @Override
    public Type getGenericType() {
        return this._field.getGenericType();
    }
    
    @Override
    public JavaType getType() {
        return this._typeContext.resolveType(this._field.getGenericType());
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this._field.getDeclaringClass();
    }
    
    @Override
    public Member getMember() {
        return this._field;
    }
    
    @Override
    public void setValue(final Object pojo, final Object value) throws IllegalArgumentException {
        try {
            this._field.set(pojo, value);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to setValue() for field " + this.getFullName() + ": " + e.getMessage(), e);
        }
    }
    
    @Override
    public Object getValue(final Object pojo) throws IllegalArgumentException {
        try {
            return this._field.get(pojo);
        }
        catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Failed to getValue() for field " + this.getFullName() + ": " + e.getMessage(), e);
        }
    }
    
    public int getAnnotationCount() {
        return this._annotations.size();
    }
    
    public boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }
    
    @Override
    public int hashCode() {
        return this._field.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedField)o)._field == this._field);
    }
    
    @Override
    public String toString() {
        return "[field " + this.getFullName() + "]";
    }
    
    Object writeReplace() {
        return new AnnotatedField(new Serialization(this._field));
    }
    
    Object readResolve() {
        final Class<?> clazz = this._serialization.clazz;
        try {
            final Field f = clazz.getDeclaredField(this._serialization.name);
            if (!f.isAccessible()) {
                ClassUtil.checkAndFixAccess(f, false);
            }
            return new AnnotatedField(null, f, null);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not find method '" + this._serialization.name + "' from Class '" + clazz.getName());
        }
    }
    
    private static final class Serialization implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected Class<?> clazz;
        protected String name;
        
        public Serialization(final Field f) {
            this.clazz = f.getDeclaringClass();
            this.name = f.getName();
        }
    }
}
