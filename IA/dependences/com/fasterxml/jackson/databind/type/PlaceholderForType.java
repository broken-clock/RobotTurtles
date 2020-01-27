// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;

public class PlaceholderForType extends TypeBase
{
    private static final long serialVersionUID = 1L;
    protected final int _ordinal;
    protected JavaType _actualType;
    
    public PlaceholderForType(final int ordinal) {
        super(Object.class, TypeBindings.emptyBindings(), TypeFactory.unknownType(), null, 1, null, null, false);
        this._ordinal = ordinal;
    }
    
    public JavaType actualType() {
        return this._actualType;
    }
    
    public void actualType(final JavaType t) {
        this._actualType = t;
    }
    
    @Override
    protected String buildCanonicalName() {
        return this.toString();
    }
    
    @Override
    public StringBuilder getGenericSignature(final StringBuilder sb) {
        return this.getErasedSignature(sb);
    }
    
    @Override
    public StringBuilder getErasedSignature(final StringBuilder sb) {
        sb.append('$').append(this._ordinal + 1);
        return sb;
    }
    
    @Override
    public JavaType withTypeHandler(final Object h) {
        return this._unsupported();
    }
    
    @Override
    public JavaType withContentTypeHandler(final Object h) {
        return this._unsupported();
    }
    
    @Override
    public JavaType withValueHandler(final Object h) {
        return this._unsupported();
    }
    
    @Override
    public JavaType withContentValueHandler(final Object h) {
        return this._unsupported();
    }
    
    @Override
    public JavaType withContentType(final JavaType contentType) {
        return this._unsupported();
    }
    
    @Override
    public JavaType withStaticTyping() {
        return this._unsupported();
    }
    
    @Override
    public JavaType refine(final Class<?> rawType, final TypeBindings bindings, final JavaType superClass, final JavaType[] superInterfaces) {
        return this._unsupported();
    }
    
    @Override
    protected JavaType _narrow(final Class<?> subclass) {
        return this._unsupported();
    }
    
    @Override
    public boolean isContainerType() {
        return false;
    }
    
    @Override
    public String toString() {
        return this.getErasedSignature(new StringBuilder()).toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this;
    }
    
    private <T> T _unsupported() {
        throw new UnsupportedOperationException("Operation should not be attempted on " + this.getClass().getName());
    }
}
