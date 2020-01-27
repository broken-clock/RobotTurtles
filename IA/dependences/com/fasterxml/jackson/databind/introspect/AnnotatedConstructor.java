// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.introspect;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import com.fasterxml.jackson.databind.JavaType;
import java.lang.reflect.Constructor;

public final class AnnotatedConstructor extends AnnotatedWithParams
{
    private static final long serialVersionUID = 1L;
    protected final Constructor<?> _constructor;
    protected Serialization _serialization;
    
    public AnnotatedConstructor(final TypeResolutionContext ctxt, final Constructor<?> constructor, final AnnotationMap classAnn, final AnnotationMap[] paramAnn) {
        super(ctxt, classAnn, paramAnn);
        if (constructor == null) {
            throw new IllegalArgumentException("Null constructor not allowed");
        }
        this._constructor = constructor;
    }
    
    protected AnnotatedConstructor(final Serialization ser) {
        super(null, null, null);
        this._constructor = null;
        this._serialization = ser;
    }
    
    @Override
    public AnnotatedConstructor withAnnotations(final AnnotationMap ann) {
        return new AnnotatedConstructor(this._typeContext, this._constructor, ann, this._paramAnnotations);
    }
    
    @Override
    public Constructor<?> getAnnotated() {
        return this._constructor;
    }
    
    public int getModifiers() {
        return this._constructor.getModifiers();
    }
    
    @Override
    public String getName() {
        return this._constructor.getName();
    }
    
    @Override
    public JavaType getType() {
        return this._typeContext.resolveType(this.getRawType());
    }
    
    @Override
    public Class<?> getRawType() {
        return this._constructor.getDeclaringClass();
    }
    
    @Override
    public int getParameterCount() {
        return this._constructor.getParameterTypes().length;
    }
    
    @Override
    public Class<?> getRawParameterType(final int index) {
        final Class<?>[] types = this._constructor.getParameterTypes();
        return (index >= types.length) ? null : types[index];
    }
    
    @Override
    public JavaType getParameterType(final int index) {
        final Type[] types = this._constructor.getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return this._typeContext.resolveType(types[index]);
    }
    
    @Deprecated
    @Override
    public Type getGenericParameterType(final int index) {
        final Type[] types = this._constructor.getGenericParameterTypes();
        if (index >= types.length) {
            return null;
        }
        return types[index];
    }
    
    @Override
    public final Object call() throws Exception {
        return this._constructor.newInstance(new Object[0]);
    }
    
    @Override
    public final Object call(final Object[] args) throws Exception {
        return this._constructor.newInstance(args);
    }
    
    @Override
    public final Object call1(final Object arg) throws Exception {
        return this._constructor.newInstance(arg);
    }
    
    @Override
    public Class<?> getDeclaringClass() {
        return this._constructor.getDeclaringClass();
    }
    
    @Override
    public Member getMember() {
        return this._constructor;
    }
    
    @Override
    public void setValue(final Object pojo, final Object value) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call setValue() on constructor of " + this.getDeclaringClass().getName());
    }
    
    @Override
    public Object getValue(final Object pojo) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot call getValue() on constructor of " + this.getDeclaringClass().getName());
    }
    
    @Override
    public String toString() {
        return "[constructor for " + this.getName() + ", annotations: " + this._annotations + "]";
    }
    
    @Override
    public int hashCode() {
        return this._constructor.getName().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (ClassUtil.hasClass(o, this.getClass()) && ((AnnotatedConstructor)o)._constructor == this._constructor);
    }
    
    Object writeReplace() {
        return new AnnotatedConstructor(new Serialization(this._constructor));
    }
    
    Object readResolve() {
        final Class<?> clazz = this._serialization.clazz;
        try {
            final Constructor<?> ctor = clazz.getDeclaredConstructor(this._serialization.args);
            if (!ctor.isAccessible()) {
                ClassUtil.checkAndFixAccess(ctor, false);
            }
            return new AnnotatedConstructor(null, ctor, null, null);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not find constructor with " + this._serialization.args.length + " args from Class '" + clazz.getName());
        }
    }
    
    private static final class Serialization implements Serializable
    {
        private static final long serialVersionUID = 1L;
        protected Class<?> clazz;
        protected Class<?>[] args;
        
        public Serialization(final Constructor<?> ctor) {
            this.clazz = ctor.getDeclaringClass();
            this.args = ctor.getParameterTypes();
        }
    }
}
